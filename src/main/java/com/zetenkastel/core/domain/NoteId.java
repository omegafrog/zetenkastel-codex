package com.zetenkastel.core.domain;

public record NoteId(NoteType type, String fileName) {

    public NoteId {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
    }

    public String pathKey() {
        return type.directory() + "/" + fileName;
    }

    public static NoteId fromPathKey(String pathKey) {
        String[] parts = pathKey.split("/", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid path key: " + pathKey);
        }
        return new NoteId(NoteType.fromDirectory(parts[0]), parts[1]);
    }
}
