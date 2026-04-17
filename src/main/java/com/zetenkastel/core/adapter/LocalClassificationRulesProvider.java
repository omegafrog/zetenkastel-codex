package com.zetenkastel.core.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetenkastel.core.port.ClassificationRulesProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class LocalClassificationRulesProvider implements ClassificationRulesProvider {

    private static final Set<String> CORE_KEYS = Set.of("id", "type", "fileName", "title", "tags", "links", "content");

    private final Path referenceRoot;
    private final ObjectMapper objectMapper;

    public LocalClassificationRulesProvider(
            @Value("${note.classification.reference-root:docs/reference}") String referenceRoot,
            ObjectMapper objectMapper
    ) {
        this.referenceRoot = Path.of(referenceRoot);
        this.objectMapper = objectMapper;
    }

    @Override
    public ClassificationRulesSnapshot load() {
        try {
            return new ClassificationRulesSnapshot(loadCanonicalMetadataKeys(), loadReferenceDocuments());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load classification rules from " + referenceRoot, e);
        }
    }

    private List<String> loadCanonicalMetadataKeys() throws IOException {
        Path unionFile = referenceRoot.resolve("property-union.json");
        JsonNode root = objectMapper.readTree(Files.readString(unionFile, StandardCharsets.UTF_8));
        Map<String, String> normalizedFromJson = new LinkedHashMap<>();
        JsonNode normalized = root.path("normalizedMapping");
        normalized.fields().forEachRemaining(entry -> normalizedFromJson.put(entry.getKey(), entry.getValue().asText()));

        LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (JsonNode node : root.path("union")) {
            String rawKey = node.asText();
            if (CORE_KEYS.contains(rawKey)) {
                continue;
            }
            keys.add(toCanonicalKey(rawKey, normalizedFromJson));
        }

        keys.add("author");
        keys.add("source");
        keys.add("url");
        keys.add("attendees");
        keys.add("deadline");
        keys.add("priority");
        keys.add("keywords");
        keys.add("next_action");
        keys.add("blockers");
        keys.add("classification_reason");
        keys.add("classification_score");
        keys.add("classification_review");
        return List.copyOf(keys);
    }

    private Map<String, String> loadReferenceDocuments() throws IOException {
        Map<String, String> documents = new LinkedHashMap<>();
        try (var stream = Files.list(referenceRoot)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(path -> {
                        try {
                            documents.put(path.getFileName().toString(), Files.readString(path, StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            throw new IllegalStateException("Failed to read reference document: " + path, e);
                        }
                    });
        }
        return documents;
    }

    private String toCanonicalKey(String rawKey, Map<String, String> normalizedFromJson) {
        if (normalizedFromJson.containsKey(rawKey)) {
            return normalizeAsciiKey(normalizedFromJson.get(rawKey));
        }
        return switch (rawKey) {
            case "저자" -> "author";
            case "출처" -> "source";
            case "URL" -> "url";
            case "참석자" -> "attendees";
            case "마감일" -> "deadline";
            case "우선순위" -> "priority";
            case "키워드" -> "keywords";
            case "다음 작업" -> "next_action";
            case "블로커" -> "blockers";
            case "최종 업데이트" -> "last_synced_at";
            case "노트 수" -> "note_count";
            default -> normalizeAsciiKey(rawKey);
        };
    }

    private String normalizeAsciiKey(String key) {
        return key.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
