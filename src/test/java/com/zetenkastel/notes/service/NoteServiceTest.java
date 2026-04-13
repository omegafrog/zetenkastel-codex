package com.zetenkastel.notes.service;

import com.zetenkastel.notes.model.CreateNoteRequest;
import com.zetenkastel.notes.model.NoteDetail;
import com.zetenkastel.notes.model.UpdateNoteRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoteServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void createAndLoadNoteByPathIdentifier() {
        NoteService service = new NoteService(tempDir.toString());

        NoteDetail created = service.create(new CreateNoteRequest(
            "inbox",
            "My First Note",
            List.of("idea", "zettel"),
            "hello [[inbox/linked.md]]",
            null
        ));

        NoteDetail loaded = service.getNote(created.path());

        assertThat(created.path()).startsWith("inbox/").endsWith(".md");
        assertThat(loaded.title()).isEqualTo("My First Note");
        assertThat(loaded.tags()).containsExactly("idea", "zettel");
        assertThat(loaded.links()).containsExactly("inbox/linked.md");
    }

    @Test
    void searchBacklinksAndGraphWorkTogether() {
        NoteService service = new NoteService(tempDir.toString());

        NoteDetail a = service.create(new CreateNoteRequest("inbox", "Alpha", List.of("java"), "see [[projects/beta.md]]", "alpha"));
        NoteDetail b = service.create(new CreateNoteRequest("projects", "Beta", List.of("spring"), "content", "beta"));

        assertThat(service.search("alpha", null)).extracting("path").contains(a.path());
        assertThat(service.search(null, "spring")).extracting("path").contains(b.path());
        assertThat(service.backlinks(b.path())).extracting("path").contains(a.path());

        var graph = service.graph();
        assertThat(graph.nodes()).hasSize(2);
        assertThat(graph.edges()).extracting("source").contains(a.path());
        assertThat(graph.edges()).extracting("target").contains(b.path());
    }

    @Test
    void updateDeleteAndRecommend() {
        NoteService service = new NoteService(tempDir.toString());

        NoteDetail base = service.create(new CreateNoteRequest("inbox", "Java Spring", List.of("java", "spring"), "rest api", "note-a"));
        NoteDetail other = service.create(new CreateNoteRequest("references", "Spring Guide", List.of("spring"), "java rest", "note-b"));

        var rec = service.recommendLinks(base.path(), 5);
        assertThat(rec).isNotEmpty();
        assertThat(rec.getFirst().path()).isEqualTo(other.path());

        NoteDetail updated = service.update(new UpdateNoteRequest(base.path(), "Java Spring Updated", List.of("java"), "rest api v2", null));
        assertThat(updated.title()).isEqualTo("Java Spring Updated");

        service.delete(updated.path());
        assertThat(service.listNotes(null)).extracting("path").doesNotContain(updated.path());
    }
}
