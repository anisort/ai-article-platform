package com.llm.dto.article;

import java.util.UUID;

public record ArticleEmbeddingDocument(
        UUID id,
        String description,
        String title,
        String author,
        String image
) {
}
