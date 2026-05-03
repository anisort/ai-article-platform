package com.articles.dto;

import java.util.UUID;

public record ArticleEmbeddingDraft(
        UUID id,
        String text,
        String title,
        String author,
        String image
) {
}
