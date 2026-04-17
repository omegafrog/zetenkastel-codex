package com.zetenkastel.core.adapter;

import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import com.zetenkastel.core.port.NoteRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class LocalFileNoteRepository implements NoteRepository {

    private final Path root;

    public LocalFileNoteRepository(@Value("${note.storage.root:data/notes}") String rootDir) {
        this.root = Path.of(rootDir);
        initDirectories();
    }

    @Override
    public Note save(Note note) {
        Path file = toPath(note.id());
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, serialize(note), StandardCharsets.UTF_8);
            return note;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save note: " + note.id().pathKey(), e);
        }
    }

    @Override
    public Optional<Note> findById(NoteId id) {
        Path file = toPath(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(deserialize(id, Files.readString(file, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read note: " + id.pathKey(), e);
        }
    }

    @Override
    public List<Note> findAll() {
        List<Note> result = new ArrayList<>();
        for (NoteType type : NoteType.values()) {
            result.addAll(findByType(type));
        }
        result.sort(Comparator.comparing(note -> note.id().pathKey()));
        return result;
    }

    @Override
    public List<Note> findByType(NoteType type) {
        Path typeDir = root.resolve(type.directory());
        if (!Files.exists(typeDir)) {
            return List.of();
        }

        try (var stream = Files.list(typeDir)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted()
                    .map(path -> {
                        String fileName = stripMarkdownExtension(path.getFileName().toString());
                        NoteId id = new NoteId(type, fileName);
                        try {
                            return deserialize(id, Files.readString(path, StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            throw new IllegalStateException("Failed to parse note: " + path, e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to list notes: " + type.directory(), e);
        }
    }

    @Override
    public List<Note> findOverdueNotes() {
        return findByType(NoteType.INBOX).stream()
                .filter(Note::isDue)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(NoteId id) {
        return Files.exists(toPath(id));
    }

    @Override
    public void deleteById(NoteId id) {
        try {
            Files.deleteIfExists(toPath(id));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete note: " + id.pathKey(), e);
        }
    }

    private void initDirectories() {
        try {
            Files.createDirectories(root);
            for (NoteType type : NoteType.values()) {
                Files.createDirectories(root.resolve(type.directory()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize note directories", e);
        }
    }

    private Path toPath(NoteId id) {
        return root.resolve(id.type().directory()).resolve(id.fileName() + ".md");
    }

    private String stripMarkdownExtension(String fileName) {
        return fileName.endsWith(".md") ? fileName.substring(0, fileName.length() - 3) : fileName;
    }

    private String serialize(Note note) {
        List<String> lines = new ArrayList<>();
        lines.add("---");
        lines.add("title: " + note.title());
        lines.add("type: " + note.id().type().directory());
        lines.add("fileName: " + note.id().fileName());
        String tags = String.join(",", note.tags());
        String links = String.join(",", note.links());
        String dueDate = note.dueDate() != null ? note.dueDate().toString() : "";
        return "title: " + note.title() + "\n"
                + "tags: " + tags + "\n"
                + "links: " + links + "\n"
                + "duedate: " + dueDate + "\n"
                + "---\n"
                + note.content();
    }

    private Note deserialize(NoteId id, String raw) {
        List<String> lines = raw.lines().collect(Collectors.toList());
        if (!lines.isEmpty() && lines.getFirst().equals("---")) {
            int secondDivider = findFrontmatterDivider(lines);
            if (secondDivider > 0) {
                return deserializeFrontmatter(id, lines, secondDivider);
            }
        }
        int divider = lines.indexOf("---");

        if (divider < 0) {
            String fallbackTitle = lines.isEmpty() ? id.fileName() : lines.getFirst();
            return new Note(id, fallbackTitle, Set.of(), raw, Set.of(), Map.of());
        }

        String title = extractHeader(lines, "title:", divider, id.fileName());
        Set<String> tags = splitCommaValues(extractHeader(lines, "tags:", divider, ""));
        Set<String> links = splitCommaValues(extractHeader(lines, "links:", divider, ""));
        String dueDateStr = extractHeader(lines, "duedate:", divider, "");
        LocalDate dueDate = dueDateStr.isBlank() ? null : LocalDate.parse(dueDateStr);
        String content = String.join("\n", lines.subList(divider + 1, lines.size()));

        return new Note(id, title, tags, content, links, dueDate);
    }

    private Note deserializeFrontmatter(NoteId id, List<String> lines, int divider) {
        Map<String, String> frontmatter = new LinkedHashMap<>();
        for (int i = 1; i < divider; i++) {
            String line = lines.get(i);
            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                continue;
            }
            String key = line.substring(0, colonIndex).strip();
            String value = line.substring(colonIndex + 1).strip();
            frontmatter.put(key, value);
        }

        String title = frontmatter.getOrDefault("title", id.fileName());
        Set<String> tags = splitCommaValues(frontmatter.getOrDefault("tags", ""));
        Set<String> links = splitCommaValues(frontmatter.getOrDefault("links", ""));
        frontmatter.remove("title");
        frontmatter.remove("type");
        frontmatter.remove("fileName");
        frontmatter.remove("tags");
        frontmatter.remove("links");

        String content = String.join("\n", lines.subList(divider + 1, lines.size()));
        return new Note(id, title, tags, content, links, frontmatter);
    }

    private int findFrontmatterDivider(List<String> lines) {
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).equals("---")) {
                return i;
            }
        }
        return -1;
    }

    private String extractHeader(List<String> lines, String key, int divider, String defaultValue) {
        for (int i = 0; i < divider; i++) {
            String line = lines.get(i);
            if (line.toLowerCase().startsWith(key)) {
                return line.substring(key.length()).strip();
            }
        }
        return defaultValue;
    }

    private Set<String> splitCommaValues(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::strip)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
