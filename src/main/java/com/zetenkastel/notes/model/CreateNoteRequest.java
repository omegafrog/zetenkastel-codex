package com.zetenkastel.notes.model;

import java.util.List;

public record CreateNoteRequest(
    String type,
    String title,
    List<String> tags,
    String content,
    String filename
) {
}
