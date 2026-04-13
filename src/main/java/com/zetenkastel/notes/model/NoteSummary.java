package com.zetenkastel.notes.model;

import java.util.List;

public record NoteSummary(
    String path,
    String type,
    String title,
    List<String> tags,
    int contentLength
) {
}
