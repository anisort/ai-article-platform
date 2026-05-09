package com.articles.dto.article;

import com.articles.entities.Article;
import com.articles.dto.user.AuthorResponse;

import java.util.UUID;

public record PublishedArticlePreviewResponse(
        UUID id,
        String title,
        String imageUrl,
        AuthorResponse author
) {
    public static PublishedArticlePreviewResponse fromEntity(Article article, AuthorResponse author) {
        return new PublishedArticlePreviewResponse(
                article.getId(),
                article.getTitle(),
                article.getImageUrl(),
                author
        );
    }
}
