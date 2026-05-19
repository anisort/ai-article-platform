package com.articles.dto.article;

import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticlePreviewResponse(
        UUID id,
        String title,
        String imageUrl,
        ZonedDateTime createdAt,
        boolean published,
        AuthorResponse author
) implements Serializable {
    public static ArticlePreviewResponse fromEntity(Article article, AuthorResponse author) {
        return new ArticlePreviewResponse(
                article.getId(),
                article.getTitle(),
                article.getImageUrl(),
                article.getCreatedAt(),
                article.isPublished(),
                author
        );
    }
}
