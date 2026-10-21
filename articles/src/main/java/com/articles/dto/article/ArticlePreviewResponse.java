package com.articles.dto.article;

import com.articles.dto.user.UserBriefResponse;
import com.articles.entities.Article;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Carries article preview response.
 */
public record ArticlePreviewResponse(
        UUID id,
        String title,
        String imageUrl,
        ZonedDateTime createdAt,
        boolean published,
        UserBriefResponse author
) implements Serializable {
/**
 * Returns from entity.
 * @param article the article
 * @param author the author
 * @return the result of the operation
 */
    public static ArticlePreviewResponse fromEntity(Article article, UserBriefResponse author) {
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
