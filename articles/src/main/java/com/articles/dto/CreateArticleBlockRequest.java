package com.articles.dto;

import com.articles.entities.BlockType;

public record CreateArticleBlockRequest(
        int position,
        BlockType type,
        String content,
        String codeLanguage,
        String code,
        String imageUrl,
        String imageCaption
) {}
