package com.articles.dto.image;

import com.articles.entities.ArticleImage;

import java.util.UUID;

/**
 * Carries article image response.
 */
public record ArticleImageResponse(
        UUID id,
        UUID articleId,
        String imageUrl
) {
/**
 * Returns from entity.
 * @param image the image
 * @return the result of the operation
 */
    public static ArticleImageResponse fromEntity(ArticleImage image) {
        return new ArticleImageResponse(image.getId(), image.getArticleId(), image.getImageUrl());
    }
}
