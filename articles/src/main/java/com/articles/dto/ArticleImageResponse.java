package com.articles.dto;

import com.articles.entities.ArticleImage;

import java.util.UUID;

public record ArticleImageResponse(
        UUID id,
        UUID articleId,
        String imageUrl
) {
    public static ArticleImageResponse fromEntity(ArticleImage image) {
        return new ArticleImageResponse(image.getId(), image.getArticleId(), image.getImageUrl());
    }
}
