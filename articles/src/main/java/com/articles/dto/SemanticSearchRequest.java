package com.articles.dto;

public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
