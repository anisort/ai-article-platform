package com.llm.dto.discovery;

public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
