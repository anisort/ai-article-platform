package com.llm.dto;

public record SemanticSearchRequest(
        String query,
        Double similarityThreshold,
        Integer topK
) {
}
