package com.zetenkastel.core.app;

import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import com.zetenkastel.core.port.ClassificationRulesProvider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class NoteClassificationService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
    private static final Pattern LABELED_VALUE_PATTERN = Pattern.compile("(?m)^(저자|출처|URL)\\s*:\\s*(.+)$");
    private static final Set<String> IDEA_TOKENS = Set.of("idea", "ideas", "아이디어", "질문", "메모", "나중에", "떠오른");
    private static final Set<String> PROJECT_TOKENS = Set.of("목표", "작업", "task", "마감", "블로커", "우선순위", "project");
    private static final Set<String> AREA_TOKENS = Set.of("정기 검토", "다음 검토", "책임 영역", "유지", "분기");
    private static final Set<String> MOC_TOKENS = Set.of("moc", "map of content", "지식 구조", "학습 경로");
    private static final Set<String> SOURCE_TOKENS = Set.of("책", "논문", "아티클", "강의", "source", "author", "출처", "저자", "url", "doi");

    private final ClassificationRulesProvider rulesProvider;

    public NoteClassificationService(ClassificationRulesProvider rulesProvider) {
        this.rulesProvider = rulesProvider;
    }

    public PreparedNote prepare(Note incoming, Note existing) {
        ClassificationRulesProvider.ClassificationRulesSnapshot snapshot = rulesProvider.load();
        Map<String, String> metadata = initializeMetadata(snapshot.canonicalMetadataKeys(), incoming, existing);
        ClassificationDecision decision = decideType(incoming, metadata, snapshot.referenceDocuments());

        metadata.put("created", firstNonBlank(metadata.get("created"), existingValue(existing, "created"), now()));
        metadata.put("updated", now());
        metadata.put("status", firstNonBlank(metadata.get("status"), defaultStatus(decision.targetType())));
        metadata.put("keywords", firstNonBlank(metadata.get("keywords"), deriveKeywords(incoming)));
        metadata.put("classification_reason", decision.reason());
        metadata.put("classification_score", String.format(Locale.ROOT, "%.2f", decision.score()));
        metadata.put("classification_review", decision.reviewRequired() ? "needs-review" : "auto");

        NoteId targetId = new NoteId(decision.targetType(), incoming.id().fileName());
        return new PreparedNote(new Note(targetId, incoming.title(), incoming.tags(), incoming.content(), incoming.links(), metadata), decision);
    }

    private Map<String, String> initializeMetadata(List<String> canonicalKeys, Note incoming, Note existing) {
        LinkedHashMap<String, String> metadata = new LinkedHashMap<>();
        for (String key : canonicalKeys) {
            metadata.put(key, "");
        }
        if (existing != null) {
            existing.metadata().forEach((key, value) -> metadata.put(canonicalizeKey(key), value == null ? "" : value.strip()));
        }
        incoming.metadata().forEach((key, value) -> metadata.put(canonicalizeKey(key), value == null ? "" : value.strip()));

        Map<String, String> extracted = extractLabeledMetadata(incoming.content());
        extracted.forEach((key, value) -> {
            if (metadata.getOrDefault(key, "").isBlank()) {
                metadata.put(key, value);
            }
        });

        if (metadata.getOrDefault("url", "").isBlank()) {
            firstUrl(incoming.content()).ifPresent(url -> metadata.put("url", url));
        }
        return metadata;
    }

    private ClassificationDecision decideType(Note note, Map<String, String> metadata, Map<String, String> referenceDocuments) {
        if (note.id().type() != NoteType.INBOX) {
            return new ClassificationDecision(note.id().type(), 1.0, "non-inbox note keeps its explicit type", false);
        }

        String combined = normalize(note.title()) + "\n" + normalize(note.content());
        boolean sourceEvidence = hasAny(metadata.get("author"), metadata.get("source"), metadata.get("url")) || containsAny(combined, SOURCE_TOKENS);

        double literatureScore = 0.0;
        if (!blank(metadata.get("author"))) {
            literatureScore += 2.0;
        }
        if (!blank(metadata.get("source"))) {
            literatureScore += 2.0;
        }
        if (!blank(metadata.get("url"))) {
            literatureScore += 2.0;
        }
        if (containsAny(combined, SOURCE_TOKENS)) {
            literatureScore += 1.5;
        }

        double fleetingScore = 0.0;
        if (!sourceEvidence) {
            fleetingScore += 1.0;
        }
        if (note.content().length() <= 240) {
            fleetingScore += 0.5;
        }
        if (containsAny(combined, IDEA_TOKENS)) {
            fleetingScore += 2.0;
        }

        double projectScore = weightedScore(metadata, combined, PROJECT_TOKENS, List.of("date_range", "deadline", "priority", "next_action", "blockers"));
        double areaScore = weightedScore(metadata, combined, AREA_TOKENS, List.of("last_reviewed_at", "next_review_at", "status"));
        double mocScore = weightedScore(metadata, combined, MOC_TOKENS, List.of("note_count", "last_synced_at"));

        List<ClassificationDecision> decisions = new ArrayList<>();
        decisions.add(new ClassificationDecision(NoteType.LITERATURE_NOTES, literatureScore, "rules: literature-notes.md + property-union source evidence", false));
        decisions.add(new ClassificationDecision(NoteType.FLEETING_NOTES, fleetingScore, "rules: fleeting-notes.md + short memo/idea evidence", false));
        decisions.add(new ClassificationDecision(NoteType.PROJECTS, projectScore, "rules: projects.md + project planning evidence", false));
        decisions.add(new ClassificationDecision(NoteType.AREA, areaScore, "rules: areas.md + review/status evidence", false));
        decisions.add(new ClassificationDecision(NoteType.MAPS_OF_CONTENT, mocScore, "rules: map-of-contents.md + structure evidence", false));

        ClassificationDecision best = decisions.stream()
                .max((left, right) -> Double.compare(left.score(), right.score()))
                .orElse(new ClassificationDecision(NoteType.INBOX, 0.0, "no classification rules matched", true));

        long bestCount = decisions.stream().filter(it -> Double.compare(it.score(), best.score()) == 0).count();
        if (best.score() < 3.0 || bestCount > 1) {
            String reason = referenceDocuments.containsKey("inbox.md")
                    ? "rules: inbox.md default fallback because evidence is ambiguous"
                    : "ambiguous evidence";
            return new ClassificationDecision(NoteType.INBOX, best.score(), reason, true);
        }
        return best;
    }

    private double weightedScore(Map<String, String> metadata, String combined, Set<String> tokens, List<String> keys) {
        double score = 0.0;
        for (String key : keys) {
            if (!blank(metadata.get(key))) {
                score += 2.0;
            }
        }
        if (containsAny(combined, tokens)) {
            score += 1.5;
        }
        return score;
    }

    private String deriveKeywords(Note note) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        note.tags().stream()
                .map(String::strip)
                .filter(value -> !value.isBlank())
                .limit(5)
                .forEach(keywords::add);

        for (String token : splitTokens(note.title() + " " + note.content())) {
            if (keywords.size() >= 5) {
                break;
            }
            keywords.add(token);
        }
        return String.join(", ", keywords);
    }

    private Set<String> splitTokens(String text) {
        String normalized = normalize(text);
        if (normalized.isBlank()) {
            return Set.of();
        }
        LinkedHashSet<String> tokens = new LinkedHashSet<>();
        for (String token : normalized.split("[^\\p{L}\\p{N}]+")) {
            if (token.length() > 1) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private Map<String, String> extractLabeledMetadata(String content) {
        LinkedHashMap<String, String> extracted = new LinkedHashMap<>();
        Matcher matcher = LABELED_VALUE_PATTERN.matcher(content == null ? "" : content);
        while (matcher.find()) {
            extracted.put(canonicalizeKey(matcher.group(1)), matcher.group(2).strip());
        }
        return extracted;
    }

    private Optional<String> firstUrl(String content) {
        Matcher matcher = URL_PATTERN.matcher(content == null ? "" : content);
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    private String canonicalizeKey(String raw) {
        return switch (raw == null ? "" : raw.strip()) {
            case "저자" -> "author";
            case "출처" -> "source";
            case "URL", "url" -> "url";
            case "완료 여부" -> "completion_status";
            case "처리 예정일", "처리 기한" -> "due_date";
            case "날짜" -> "meeting_date";
            case "참석자" -> "attendees";
            case "장소/링크" -> "location_or_link";
            case "관련 프로젝트" -> "related_projects";
            case "마지막 검토" -> "last_reviewed_at";
            case "다음 검토" -> "next_review_at";
            case "기간" -> "date_range";
            case "마감일" -> "deadline";
            case "우선순위" -> "priority";
            case "키워드" -> "keywords";
            case "다음 작업" -> "next_action";
            case "블로커" -> "blockers";
            case "최종 업데이트" -> "last_synced_at";
            case "노트 수" -> "note_count";
            default -> raw == null ? "" : raw.strip().toLowerCase(Locale.ROOT);
        };
    }

    private boolean containsAny(String source, Set<String> tokens) {
        return tokens.stream().anyMatch(source::contains);
    }

    private boolean hasAny(String... values) {
        for (String value : values) {
            if (!blank(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String existingValue(Note existing, String key) {
        return existing == null ? "" : existing.metadata().getOrDefault(key, "");
    }

    private String defaultStatus(NoteType type) {
        return switch (type) {
            case INBOX, FLEETING_NOTES -> "unprocessed";
            case LITERATURE_NOTES -> "draft";
            case PROJECTS, AREA, MAPS_OF_CONTENT -> "active";
            default -> "active";
        };
    }

    private String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (!blank(candidate)) {
                return candidate.strip();
            }
        }
        return "";
    }

    private String now() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }

    public record PreparedNote(Note note, ClassificationDecision decision) { }

    public record ClassificationDecision(NoteType targetType, double score, String reason, boolean reviewRequired) { }
}
