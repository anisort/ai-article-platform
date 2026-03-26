package com.articles.dto;

import com.articles.entities.ArticleBlock;
import com.articles.entities.BlockType;

import java.util.UUID;

public record ArticleBlockResponse(
        UUID id,
        int position,
        BlockType type,
        String content,
        String codeLanguage,
        String code,
        String imageUrl,
        String imageCaption
) {
    public static ArticleBlockResponse fromEntity(ArticleBlock b) {
        return new ArticleBlockResponse(
                b.getId(),
                b.getPosition(),
                b.getType(),
                b.getContent(),
                b.getCodeLanguage(),
                b.getCode(),
                b.getImageUrl(),
                b.getImageCaption()
        );
    }
}