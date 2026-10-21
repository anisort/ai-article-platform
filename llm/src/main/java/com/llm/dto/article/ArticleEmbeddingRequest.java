package com.llm.dto.article;

import java.util.UUID;

/**
 * Carries article embedding request.
 */
public record ArticleEmbeddingRequest(
        UUID id,
        String content,
        String title,
        String author,
        String image,
        boolean published
) {
}

