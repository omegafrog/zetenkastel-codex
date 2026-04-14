package com.zetenkastel.core.app;

import com.zetenkastel.core.domain.LinkMode;
import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import com.zetenkastel.core.port.NoteRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note create(Note note, LinkMode linkMode) {
        if (noteRepository.existsById(note.id())) {
            throw new IllegalArgumentException("Note already exists: " + note.id().pathKey());
        }
        Note saved = noteRepository.save(note);
        return applyLinkMode(saved, linkMode);
    }

    public Note update(Note note, LinkMode linkMode) {
        if (!noteRepository.existsById(note.id())) {
            throw new IllegalArgumentException("Note does not exist: " + note.id().pathKey());
        }
        Note saved = noteRepository.save(note);
        return applyLinkMode(saved, linkMode);
    }

    public void delete(NoteId id) {
        noteRepository.deleteById(id);
        List<Note> allNotes = noteRepository.findAll();
        for (Note note : allNotes) {
            if (note.links().contains(id.pathKey())) {
                Set<String> updatedLinks = new LinkedHashSet<>(note.links());
                updatedLinks.remove(id.pathKey());
                noteRepository.save(note.withLinks(updatedLinks));
            }
        }
    }

    public Note get(NoteId id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id.pathKey()));
    }

    public List<Note> list(NoteType type) {
        return type == null ? noteRepository.findAll() : noteRepository.findByType(type);
    }

    public List<Note> search(String query, String tag) {
        String normalizedQuery = normalize(query);
        String normalizedTag = normalize(tag);

        Predicate<Note> queryMatch = note -> normalizedQuery.isBlank() ||
                normalize(note.title()).contains(normalizedQuery) ||
                normalize(note.content()).contains(normalizedQuery) ||
                note.tags().stream().map(this::normalize).anyMatch(it -> it.contains(normalizedQuery));

        Predicate<Note> tagMatch = note -> normalizedTag.isBlank() ||
                note.tags().stream().map(this::normalize).anyMatch(normalizedTag::equals);

        return noteRepository.findAll().stream()
                .filter(queryMatch.and(tagMatch))
                .sorted(Comparator.comparing(it -> it.id().pathKey()))
                .toList();
    }

    public List<Note> backlinks(NoteId id) {
        String target = id.pathKey();
        return noteRepository.findAll().stream()
                .filter(note -> note.links().contains(target))
                .sorted(Comparator.comparing(it -> it.id().pathKey()))
                .toList();
    }

    public List<ScoredNote> recommendations(NoteId id) {
        Note source = get(id);
        Set<String> sourceTokens = tokenize(source);
        Set<String> existingLinks = source.links();

        return noteRepository.findAll().stream()
                .filter(candidate -> !candidate.id().equals(source.id()))
                .filter(candidate -> !existingLinks.contains(candidate.id().pathKey()))
                .map(candidate -> new ScoredNote(candidate, jaccard(sourceTokens, tokenize(candidate))))
                .filter(scored -> scored.score() > 0.0)
                .sorted(Comparator.comparing(ScoredNote::score).reversed())
                .limit(10)
                .toList();
    }

    public GraphView graphView() {
        List<Note> notes = noteRepository.findAll();
        List<GraphNode> nodes = notes.stream()
                .map(note -> new GraphNode(note.id().pathKey(), note.title(), note.id().type().directory()))
                .toList();

        Set<String> validIds = nodes.stream().map(GraphNode::id).collect(Collectors.toSet());
        List<GraphEdge> edges = new ArrayList<>();

        for (Note note : notes) {
            for (String linked : note.links()) {
                if (validIds.contains(linked)) {
                    edges.add(new GraphEdge(note.id().pathKey(), linked));
                }
            }
        }

        return new GraphView(nodes, edges);
    }

    private Note applyLinkMode(Note saved, LinkMode mode) {
        if (mode != LinkMode.AUTO_CONNECT) {
            return saved;
        }

        Set<String> merged = new LinkedHashSet<>(saved.links());
        recommendations(saved.id()).stream()
                .map(scored -> scored.note().id().pathKey())
                .forEach(merged::add);

        Note updated = saved.withLinks(merged);
        return noteRepository.save(updated);
    }

    private double jaccard(Set<String> first, Set<String> second) {
        if (first.isEmpty() || second.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(first);
        intersection.retainAll(second);

        Set<String> union = new HashSet<>(first);
        union.addAll(second);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private Set<String> tokenize(Note note) {
        Set<String> tokens = new LinkedHashSet<>();
        tokens.addAll(splitTokens(note.title()));
        tokens.addAll(splitTokens(note.content()));
        for (String tag : note.tags()) {
            tokens.add(normalize(tag));
        }
        return tokens;
    }

    private Set<String> splitTokens(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
                .map(String::strip)
                .filter(token -> token.length() > 1)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).strip();
    }

    public record ScoredNote(Note note, double score) { }

    public record GraphView(List<GraphNode> nodes, List<GraphEdge> edges) { }

    public record GraphNode(String id, String title, String type) { }

    public record GraphEdge(String source, String target) { }
}
