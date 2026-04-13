package com.zetenkastel.notes.model;

public record Recommendation(
    String path,
    String title,
    double score
) {
}
