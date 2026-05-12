package com.articles.dto.ai;

public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
