package com.articles.dto.article;

import com.articles.entities.Article;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        String title,
        String imageUrl,
        String summary,
        String markdownContent,
        ZonedDateTime createdAt,
        boolean published,
        long likesCount,
        boolean likedByRequester
) implements Serializable {
    public static ArticleResponse fromEntity(Article a, long likesCount, boolean likedByRequester) {
        return new ArticleResponse(
                a.getId(),
                a.getTitle(),
                a.getImageUrl(),
                a.getSummary(),
                a.getMarkdownContent(),
                a.getCreatedAt(),
                a.isPublished(),
                likesCount,
                likedByRequester
        );
    }
}
