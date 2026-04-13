package com.zetenkastel.notes.service;

import com.zetenkastel.notes.model.CreateNoteRequest;
import com.zetenkastel.notes.model.GraphView;
import com.zetenkastel.notes.model.NoteDetail;
import com.zetenkastel.notes.model.NoteSummary;
import com.zetenkastel.notes.model.NoteType;
import com.zetenkastel.notes.model.Recommendation;
import com.zetenkastel.notes.model.UpdateNoteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NoteService {
    private static final Pattern WIKI_LINK = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");

    private final Path root;

    public NoteService(@Value("${notes.storage.root:./data/notes}") String storageRoot) {
        this.root = Paths.get(storageRoot).toAbsolutePath().normalize();
        initTypeDirectories();
    }

    public List<NoteSummary> listNotes(String typeValue) {
        Stream<Path> walkStream = null;
        try {
            Path searchRoot = typeValue == null || typeValue.isBlank()
                ? root
                : root.resolve(NoteType.fromInput(typeValue).name());
            if (!Files.exists(searchRoot)) {
                return List.of();
            }

            walkStream = Files.walk(searchRoot);
            return walkStream
                .filter(Files::isRegularFile)
                .filter(this::isNoteFile)
                .map(this::readFileSafe)
                .filter(n -> n != null)
                .map(NoteService::toSummary)
                .sorted(Comparator.comparing(NoteSummary::path))
                .toList();
        } catch (IOException e) {
            throw new IllegalStateException("failed to list notes", e);
        } finally {
            if (walkStream != null) {
                walkStream.close();
            }
        }
    }

    public NoteDetail getNote(String pathValue) {
        Path filePath = resolveNotePath(pathValue);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("note not found: " + pathValue);
        }
        return readFile(filePath);
    }

    public NoteDetail create(CreateNoteRequest request) {
        NoteType type = NoteType.fromInput(request.type());
        String title = requireText(request.title(), "title");
        String filename = sanitizeFilename(request.filename() == null || request.filename().isBlank() ? title : request.filename()) + ".md";

        Path typeDir = root.resolve(type.name());
        Path filePath = typeDir.resolve(filename).normalize();
        validateInsideRoot(filePath);

        if (Files.exists(filePath)) {
            throw new IllegalArgumentException("note already exists: " + toRelativePath(filePath));
        }
        writeNote(filePath, title, normalizeTags(request.tags()), request.content());
        return readFile(filePath);
    }

    public NoteDetail update(UpdateNoteRequest request) {
        String sourcePath = requireText(request.path(), "path");
        Path filePath = resolveNotePath(sourcePath);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("note not found: " + sourcePath);
        }

        NoteDetail existing = readFile(filePath);
        String title = request.title() == null ? existing.title() : request.title().trim();
        List<String> tags = request.tags() == null ? existing.tags() : normalizeTags(request.tags());
        String content = request.content() == null ? existing.content() : request.content();

        Path target = filePath;
        if (request.newPath() != null && !request.newPath().isBlank()) {
            target = resolveNotePath(request.newPath());
            if (!filePath.equals(target)) {
                if (Files.exists(target)) {
                    throw new IllegalArgumentException("target already exists: " + request.newPath());
                }
                try {
                    Files.createDirectories(target.getParent());
                    Files.move(filePath, target, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    throw new IllegalStateException("failed to move note", e);
                }
            }
        }

        writeNote(target, title, tags, content);
        return readFile(target);
    }

    public void delete(String pathValue) {
        Path filePath = resolveNotePath(pathValue);
        try {
            if (!Files.deleteIfExists(filePath)) {
                throw new IllegalArgumentException("note not found: " + pathValue);
            }
        } catch (IOException e) {
            throw new IllegalStateException("failed to delete note", e);
        }
    }

    public List<NoteSummary> search(String q, String tag) {
        String query = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String tagQuery = tag == null ? "" : tag.trim().toLowerCase(Locale.ROOT);

        return listNotes(null).stream()
            .map(n -> getNote(n.path()))
            .filter(note -> {
                boolean matchedQ = query.isBlank()
                    || note.title().toLowerCase(Locale.ROOT).contains(query)
                    || note.content().toLowerCase(Locale.ROOT).contains(query);
                boolean matchedTag = tagQuery.isBlank() || note.tags().stream().anyMatch(t -> t.toLowerCase(Locale.ROOT).equals(tagQuery));
                return matchedQ && matchedTag;
            })
            .map(NoteService::toSummary)
            .toList();
    }

    public List<NoteSummary> backlinks(String pathValue) {
        return listNotes(null).stream()
            .map(n -> getNote(n.path()))
            .filter(note -> note.links().contains(pathValue))
            .map(NoteService::toSummary)
            .toList();
    }

    public GraphView graph() {
        List<NoteDetail> notes = listNotes(null).stream().map(n -> getNote(n.path())).toList();
        List<GraphView.GraphNode> nodes = notes.stream()
            .map(n -> new GraphView.GraphNode(n.path(), n.title(), n.type()))
            .toList();

        List<GraphView.GraphEdge> edges = new ArrayList<>();
        Set<String> existingIds = notes.stream().map(NoteDetail::path).collect(Collectors.toSet());

        for (NoteDetail note : notes) {
            for (String link : note.links()) {
                if (existingIds.contains(link)) {
                    edges.add(new GraphView.GraphEdge(note.path(), link));
                }
            }
        }
        return new GraphView(nodes, edges);
    }

    public List<Recommendation> recommendLinks(String pathValue, int limit) {
        NoteDetail base = getNote(pathValue);
        Set<String> baseTokens = tokenize(base.title() + " " + String.join(" ", base.tags()) + " " + base.content());

        return listNotes(null).stream()
            .map(s -> getNote(s.path()))
            .filter(n -> !n.path().equals(base.path()))
            .map(n -> new Recommendation(n.path(), n.title(), score(baseTokens, tokenize(n.title() + " " + String.join(" ", n.tags()) + " " + n.content()))))
            .filter(r -> r.score() > 0)
            .sorted((a, b) -> Double.compare(b.score(), a.score()))
            .limit(Math.max(limit, 1))
            .toList();
    }

    private static double score(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        if (inter.isEmpty()) {
            return 0.0;
        }
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) inter.size() / union.size();
    }

    private static Set<String> tokenize(String text) {
        return Stream.of(text.toLowerCase(Locale.ROOT).split("[^a-z0-9가-힣_]+"))
            .filter(s -> s.length() >= 2)
            .collect(Collectors.toSet());
    }

    private void initTypeDirectories() {
        for (NoteType type : NoteType.values()) {
            try {
                Files.createDirectories(root.resolve(type.name()));
            } catch (IOException e) {
                throw new IllegalStateException("failed to initialize note directories", e);
            }
        }
    }

    private static NoteSummary toSummary(NoteDetail note) {
        return new NoteSummary(note.path(), note.type(), note.title(), note.tags(), note.content().length());
    }

    private NoteDetail readFileSafe(Path path) {
        try {
            return readFile(path);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private NoteDetail readFile(Path path) {
        try {
            String raw = Files.readString(path, StandardCharsets.UTF_8);
            Map<String, String> meta = parseMeta(raw);
            String content = parseContent(raw);
            String title = meta.getOrDefault("title", path.getFileName().toString());
            List<String> tags = parseTags(meta.getOrDefault("tags", ""));
            String rel = toRelativePath(path);
            return new NoteDetail(rel, rel.split("/")[0], title, tags, content, extractLinks(content));
        } catch (IOException e) {
            throw new IllegalStateException("failed to read note", e);
        }
    }

    private void writeNote(Path path, String title, List<String> tags, String content) {
        try {
            Files.createDirectories(path.getParent());
            String body = "---\n" +
                "title: " + title.replace("\n", " ") + "\n" +
                "tags: " + String.join(",", tags) + "\n" +
                "---\n" +
                (content == null ? "" : content);
            Files.writeString(path, body, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("failed to write note", e);
        }
    }

    private Path resolveNotePath(String pathValue) {
        String normalized = requireText(pathValue, "path").replace('\\', '/');
        Path path = root.resolve(normalized).normalize();
        validateInsideRoot(path);
        return path;
    }

    private void validateInsideRoot(Path path) {
        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("path escapes root");
        }
    }

    private String toRelativePath(Path path) {
        return root.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/');
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    private static String sanitizeFilename(String input) {
        String slug = input.trim().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9가-힣\\-_ ]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
        if (slug.isBlank()) {
            throw new IllegalArgumentException("filename is empty after sanitization");
        }
        return slug;
    }

    private static Map<String, String> parseMeta(String raw) {
        Map<String, String> meta = new HashMap<>();
        if (!raw.startsWith("---\n")) {
            return meta;
        }
        int end = raw.indexOf("\n---\n", 4);
        if (end < 0) {
            return meta;
        }
        String block = raw.substring(4, end);
        for (String line : block.split("\n")) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                meta.put(line.substring(0, idx).trim().toLowerCase(Locale.ROOT), line.substring(idx + 1).trim());
            }
        }
        return meta;
    }

    private static String parseContent(String raw) {
        if (!raw.startsWith("---\n")) {
            return raw;
        }
        int end = raw.indexOf("\n---\n", 4);
        if (end < 0) {
            return raw;
        }
        return raw.substring(end + 5);
    }

    private static List<String> parseTags(String rawTags) {
        if (rawTags == null || rawTags.isBlank()) {
            return List.of();
        }
        return Stream.of(rawTags.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .distinct()
            .toList();
    }

    private static List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream().map(String::trim).filter(s -> !s.isBlank()).distinct().toList();
    }

    private static List<String> extractLinks(String content) {
        Matcher matcher = WIKI_LINK.matcher(content == null ? "" : content);
        List<String> links = new ArrayList<>();
        while (matcher.find()) {
            links.add(matcher.group(1).trim());
        }
        return links;
    }

    private boolean isNoteFile(Path p) {
        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".md") || name.endsWith(".txt");
    }
}
