package com.zetenkastel.core.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record Note(
        NoteId id,
        String title,
        Set<String> tags,
        String content,
        Set<String> links
) {

    public Note {
        Objects.requireNonNull(id, "id must not be null");
        title = title == null ? "" : title.strip();
        content = content == null ? "" : content;
        tags = tags == null ? Set.of() : new LinkedHashSet<>(tags);
        links = links == null ? Set.of() : new LinkedHashSet<>(links);
    }

    public Note withLinks(Set<String> updatedLinks) {
        return new Note(id, title, tags, content, updatedLinks);
    }
}
