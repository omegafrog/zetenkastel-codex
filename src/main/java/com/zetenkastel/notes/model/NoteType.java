package com.zetenkastel.notes.model;

import java.util.Arrays;

public enum NoteType {
    inbox,
    fleeting_notes,
    literature_notes,
    projects,
    area,
    archives,
    maps_of_content,
    references,
    templates,
    attachments;

    public static NoteType fromInput(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("type is required");
        }
        String normalized = value.trim().toLowerCase().replace(' ', '_').replace('-', '_').replace('/', '_');
        return Arrays.stream(values())
            .filter(v -> v.name().equals(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("unknown type: " + value));
    }
}
