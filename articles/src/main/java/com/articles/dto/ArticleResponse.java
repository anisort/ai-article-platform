package com.articles.dto;

import com.articles.entities.Article;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        String title,
        String imageUrl,
        String summary,
        String markdownContent,
        ZonedDateTime createdAt,
        boolean published
) {
    public static ArticleResponse fromEntity(Article a) {
        return new ArticleResponse(
                a.getId(),
                a.getTitle(),
                a.getImageUrl(),
                a.getSummary(),
                a.getMarkdownContent(),
                a.getCreatedAt(),
                a.isPublished()
        );
    }
}
