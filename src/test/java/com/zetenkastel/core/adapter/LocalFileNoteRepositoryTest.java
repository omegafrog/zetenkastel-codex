package com.zetenkastel.core.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LocalFileNoteRepositoryTest {

    @Test
    void shouldReadLegacyHeaderFormat() throws IOException {
        Path root = Files.createTempDirectory("zettel-legacy-");
        Files.createDirectories(root.resolve("inbox"));
        Files.writeString(
                root.resolve("inbox/legacy.md"),
                """
                title: Legacy Note
                tags: spring,graph
                links: inbox/other
                ---
                old content
                """,
                StandardCharsets.UTF_8
        );

        LocalFileNoteRepository repository = new LocalFileNoteRepository(root.toString());
        Note note = repository.findById(new NoteId(NoteType.INBOX, "legacy")).orElseThrow();

        assertEquals("Legacy Note", note.title());
        assertEquals(Set.of("spring", "graph"), note.tags());
        assertEquals(Set.of("inbox/other"), note.links());
        assertTrue(note.metadata().isEmpty());
    }

    @Test
    void shouldRoundTripFrontmatterMetadata() throws IOException {
        Path root = Files.createTempDirectory("zettel-frontmatter-");
        LocalFileNoteRepository repository = new LocalFileNoteRepository(root.toString());
        Note note = new Note(
                new NoteId(NoteType.INBOX, "frontmatter"),
                "Frontmatter",
                Set.of("tag-a"),
                "body",
                Set.of("inbox/link"),
                Map.of(
                        "author", "Martin Fowler",
                        "source", "Refactoring",
                        "url", "https://martinfowler.com",
                        "status", "draft"
                )
        );

        repository.save(note);
        String raw = Files.readString(root.resolve("inbox/frontmatter.md"), StandardCharsets.UTF_8);
        Note loaded = repository.findById(new NoteId(NoteType.INBOX, "frontmatter")).orElseThrow();

        assertTrue(raw.startsWith("---\n"));
        assertTrue(raw.contains("author: Martin Fowler"));
        assertEquals("Martin Fowler", loaded.metadata().get("author"));
        assertEquals("draft", loaded.metadata().get("status"));
        assertEquals(Set.of("tag-a"), loaded.tags());
    }
}
