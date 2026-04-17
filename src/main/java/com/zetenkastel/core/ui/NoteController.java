package com.zetenkastel.core.ui;

import com.zetenkastel.core.app.InboxTriageService;
import com.zetenkastel.core.app.NoteService;
import com.zetenkastel.core.domain.LinkMode;
import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;
    private final InboxTriageService inboxTriageService;

    public NoteController(NoteService noteService, InboxTriageService inboxTriageService) {
        this.noteService = noteService;
        this.inboxTriageService = inboxTriageService;
    }

    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@RequestBody UpsertNoteRequest request,
                               @RequestParam(name = "linkMode", required = false) String linkMode) {
        Note note = noteFrom(request);
        return NoteResponse.from(noteService.create(note, LinkMode.fromNullable(linkMode)));
    }

    @PutMapping("/notes/{type}/{fileName}")
    public NoteResponse update(@PathVariable("type") String type,
                               @PathVariable("fileName") String fileName,
                               @RequestBody UpsertNoteRequest request,
                               @RequestParam(name = "linkMode", required = false) String linkMode) {
        Note note = new Note(
                new NoteId(NoteType.fromDirectory(type), fileName),
                request.title(),
                request.tags(),
                request.content(),
                request.links(),
                request.metadata()
        );
        return NoteResponse.from(noteService.update(note, LinkMode.fromNullable(linkMode)));
    }

    @GetMapping("/notes/{type}/{fileName}")
    public NoteResponse get(@PathVariable("type") String type,
                            @PathVariable("fileName") String fileName) {
        return NoteResponse.from(noteService.get(new NoteId(NoteType.fromDirectory(type), fileName)));
    }

    @DeleteMapping("/notes/{type}/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("type") String type,
                       @PathVariable("fileName") String fileName) {
        noteService.delete(new NoteId(NoteType.fromDirectory(type), fileName));
    }

    @GetMapping("/notes")
    public List<NoteResponse> list(@RequestParam(name = "type", required = false) String type) {
        NoteType noteType = type == null || type.isBlank() ? null : NoteType.fromDirectory(type);
        return noteService.list(noteType).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/notes/search")
    public List<NoteResponse> search(@RequestParam(name = "q", required = false) String query,
                                     @RequestParam(name = "tag", required = false) String tag) {
        return noteService.search(query, tag).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/notes/{type}/{fileName}/backlinks")
    public List<NoteResponse> backlinks(@PathVariable("type") String type,
                                        @PathVariable("fileName") String fileName) {
        NoteId id = new NoteId(NoteType.fromDirectory(type), fileName);
        return noteService.backlinks(id).stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/notes/{type}/{fileName}/recommendations")
    public List<RecommendationResponse> recommendations(@PathVariable("type") String type,
                                                        @PathVariable("fileName") String fileName) {
        NoteId id = new NoteId(NoteType.fromDirectory(type), fileName);
        return noteService.recommendations(id).stream()
                .map(scored -> new RecommendationResponse(NoteResponse.from(scored.note()), scored.score()))
                .toList();
    }

    @GetMapping("/graph")
    public NoteService.GraphView graphView() {
        return noteService.graphView();
    }

    @GetMapping("/note-types")
    public List<String> noteTypes() {
        return Arrays.stream(NoteType.values()).map(NoteType::directory).toList();
    }

    @PostMapping("/triage/{type}/{fileName}")
    public String triage(@PathVariable("type") String type,
                        @PathVariable("fileName") String fileName) {
        String pathKey = type + "/" + fileName;
        inboxTriageService.triageNoteManually(pathKey);
        return "Triage completed for " + pathKey;
    }

    @PostMapping("/triage/overdue")
    public String triageAllOverdue() {
        inboxTriageService.triageOverdueNotes();
        return "Triage completed for all overdue notes";
    }

    private Note noteFrom(UpsertNoteRequest request) {
        NoteType type = NoteType.fromDirectory(request.type());
        NoteId id = new NoteId(type, request.fileName());
        return new Note(id, request.title(), request.tags(), request.content(), request.links(), request.metadata());
    }

    public record UpsertNoteRequest(
            String type,
            String fileName,
            String title,
            Set<String> tags,
            String content,
            Set<String> links,
            Map<String, String> metadata
    ) { }

    public record NoteResponse(
            String id,
            String type,
            String fileName,
            String title,
            Set<String> tags,
            String content,
            Set<String> links,
            Map<String, String> metadata
    ) {
        static NoteResponse from(Note note) {
            return new NoteResponse(
                    note.id().pathKey(),
                    note.id().type().directory(),
                    note.id().fileName(),
                    note.title(),
                    note.tags(),
                    note.content(),
                    note.links(),
                    note.metadata()
            );
        }
    }

    public record RecommendationResponse(NoteResponse note, double score) { }
}
