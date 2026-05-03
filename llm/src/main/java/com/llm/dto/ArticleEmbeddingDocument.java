package com.llm.dto;

import java.util.UUID;

public record ArticleEmbeddingDocument(
        UUID id,
        String description,
        String title,
        String author,
        String image
) {
}
