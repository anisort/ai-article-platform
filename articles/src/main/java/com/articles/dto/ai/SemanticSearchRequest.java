package com.articles.dto.ai;

/**
 * Carries semantic search request.
 */
public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
