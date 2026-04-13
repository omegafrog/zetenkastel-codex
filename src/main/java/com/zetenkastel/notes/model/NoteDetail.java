package com.zetenkastel.notes.model;

import java.util.List;

public record NoteDetail(
    String path,
    String type,
    String title,
    List<String> tags,
    String content,
    List<String> links
) {
}
