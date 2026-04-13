package com.zetenkastel.core.domain;

import java.util.Arrays;

public enum NoteType {
    INBOX("inbox"),
    FLEETING_NOTES("fleeting-notes"),
    LITERATURE_NOTES("literature-notes"),
    PROJECTS("projects"),
    AREA("area"),
    ARCHIVES("archives"),
    MAPS_OF_CONTENT("maps-of-content"),
    REFERENCES("references"),
    TEMPLATES("templates"),
    ATTACHMENTS("attachments");

    private final String directory;

    NoteType(String directory) {
        this.directory = directory;
    }

    public String directory() {
        return directory;
    }

    public static NoteType fromDirectory(String raw) {
        return Arrays.stream(values())
                .filter(value -> value.directory.equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported note type: " + raw));
    }
}
