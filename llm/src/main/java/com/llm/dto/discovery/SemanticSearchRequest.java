package com.llm.dto.discovery;

/**
 * Carries semantic search request.
 */
public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
