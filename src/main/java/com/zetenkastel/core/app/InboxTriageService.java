package com.zetenkastel.core.app;

import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import com.zetenkastel.core.port.LlmPort;
import com.zetenkastel.core.port.NoteRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InboxTriageService {

    private static final Logger log = LoggerFactory.getLogger(InboxTriageService.class);

    private final NoteRepository noteRepository;
    private final LlmPort llmPort;

    public InboxTriageService(NoteRepository noteRepository, LlmPort llmPort) {
        this.noteRepository = noteRepository;
        this.llmPort = llmPort;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void triageOverdueNotes() {
        log.info("Starting inbox triage for overdue notes");
        List<Note> overdueNotes = noteRepository.findOverdueNotes();
        log.info("Found {} overdue notes", overdueNotes.size());

        for (Note note : overdueNotes) {
            triageNote(note);
        }
    }

    public void triageNoteManually(String pathKey) {
        NoteId id = NoteId.fromPathKey(pathKey);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + pathKey));
        triageNote(note);
    }

    public void triageNote(Note note) {
        log.info("Triage note: {}", note.id().pathKey());

        LlmPort.LlmResponse response = llmPort.analyze(
                note.content(),
                "Analyze this inbox note and decide what to do. Consider if it's actionable, informational, or can be archived."
        );

        log.info("LLM decision: {} - {}", response.action(), response.reason());

        switch (response.action()) {
            case MOVE -> moveNote(note, response.targetNoteType());
            case REWRITE -> rewriteNote(note, response.newContent());
            case ARCHIVE -> moveNote(note, "archives");
            case KEEP -> log.info("Keeping note in inbox: {}", note.id().pathKey());
        }
    }

    private void moveNote(Note note, String targetType) {
        if (targetType == null || targetType.isBlank()) {
            log.warn("Cannot move note: target type is empty");
            return;
        }

        try {
            NoteType type = NoteType.fromDirectory(targetType);
            NoteId newId = new NoteId(type, note.id().fileName());
            Note movedNote = new Note(
                    newId,
                    note.title(),
                    note.tags(),
                    note.content(),
                    note.links(),
                    null
            );
            noteRepository.save(movedNote);
            noteRepository.deleteById(note.id());
            log.info("Moved note from inbox to {}: {}", targetType, note.id().pathKey());
        } catch (Exception e) {
            log.error("Failed to move note: {}", e.getMessage());
        }
    }

    private void rewriteNote(Note note, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            log.warn("Cannot rewrite note: new content is empty");
            return;
        }

        Note rewritten = new Note(
                note.id(),
                note.title(),
                note.tags(),
                newContent,
                note.links(),
                null
        );
        noteRepository.save(rewritten);
        log.info("Rewrote note content: {}", note.id().pathKey());
    }
}