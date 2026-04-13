package com.zetenkastel.core.port;

import com.zetenkastel.core.domain.Note;
import com.zetenkastel.core.domain.NoteId;
import com.zetenkastel.core.domain.NoteType;
import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    Note save(Note note);

    Optional<Note> findById(NoteId id);

    List<Note> findAll();

    List<Note> findByType(NoteType type);

    boolean existsById(NoteId id);

    void deleteById(NoteId id);
}
