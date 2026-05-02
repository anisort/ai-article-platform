package com.articles.dto;

import com.articles.entities.Article;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        String title,
        String imageUrl,
        String summary,
        ZonedDateTime createdAt,
        List<ArticleBlockResponse> blocks,
        boolean published
) {
    public static ArticleResponse fromEntity(Article a) {
        return new ArticleResponse(
                a.getId(),
                a.getTitle(),
                a.getImageUrl(),
                a.getSummary(),
                a.getCreatedAt(),
                a.getBlocks().stream()
                        .map(ArticleBlockResponse::fromEntity)
                        .toList(),
                a.isPublished()
        );
    }
}
