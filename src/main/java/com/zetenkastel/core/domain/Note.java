package com.zetenkastel.core.domain;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record Note(
        NoteId id,
        String title,
        Set<String> tags,
        String content,
        Set<String> links,
        Map<String, String> metadata
) {

    public Note {
        Objects.requireNonNull(id, "id must not be null");
        title = title == null ? "" : title.strip();
        content = content == null ? "" : content;
        tags = tags == null ? Set.of() : new LinkedHashSet<>(tags);
        links = links == null ? Set.of() : new LinkedHashSet<>(links);
        metadata = metadata == null ? Map.of() : new LinkedHashMap<>(metadata);
    }

    public Note(NoteId id, String title, Set<String> tags, String content, Set<String> links) {
        this(id, title, tags, content, links, Map.of());
    }

    public Note withLinks(Set<String> updatedLinks) {
        return new Note(id, title, tags, content, updatedLinks, metadata);
    }

    public Note withMetadata(Map<String, String> updatedMetadata) {
        return new Note(id, title, tags, content, links, updatedMetadata);
    }
}
