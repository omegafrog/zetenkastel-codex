package com.zetenkastel.notes.model;

import java.util.List;

public record UpdateNoteRequest(
    String path,
    String title,
    List<String> tags,
    String content,
    String newPath
) {
}
