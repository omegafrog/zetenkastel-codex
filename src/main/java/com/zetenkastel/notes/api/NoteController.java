package com.zetenkastel.notes.api;

import com.zetenkastel.notes.model.CreateNoteRequest;
import com.zetenkastel.notes.model.GraphView;
import com.zetenkastel.notes.model.NoteDetail;
import com.zetenkastel.notes.model.NoteSummary;
import com.zetenkastel.notes.model.Recommendation;
import com.zetenkastel.notes.model.UpdateNoteRequest;
import com.zetenkastel.notes.service.NoteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes")
    public List<NoteSummary> listNotes(@RequestParam(required = false) String type) {
        return noteService.listNotes(type);
    }

    @GetMapping("/notes/detail")
    public NoteDetail getNote(@RequestParam String path) {
        return noteService.getNote(path);
    }

    @PostMapping("/notes")
    public NoteDetail create(@RequestBody CreateNoteRequest request) {
        return noteService.create(request);
    }

    @PutMapping("/notes")
    public NoteDetail update(@RequestBody UpdateNoteRequest request) {
        return noteService.update(request);
    }

    @DeleteMapping("/notes")
    public Map<String, String> delete(@RequestParam String path) {
        noteService.delete(path);
        return Map.of("status", "deleted", "path", path);
    }

    @GetMapping("/search")
    public List<NoteSummary> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) String tag) {
        return noteService.search(q, tag);
    }

    @GetMapping("/backlinks")
    public List<NoteSummary> backlinks(@RequestParam String path) {
        return noteService.backlinks(path);
    }

    @GetMapping("/graph")
    public GraphView graph() {
        return noteService.graph();
    }

    @GetMapping("/links/recommend")
    public List<Recommendation> recommend(@RequestParam String path,
                                          @RequestParam(defaultValue = "5") int limit) {
        return noteService.recommendLinks(path, Math.max(1, limit));
    }
}
