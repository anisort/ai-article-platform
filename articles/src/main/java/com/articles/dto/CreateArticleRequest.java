package com.articles.dto;

public record CreateArticleRequest(
        String title,
        String imageUrl,
        boolean published,
        String markdownContent
) {}

