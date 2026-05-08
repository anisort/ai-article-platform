package com.articles.dto;

import java.util.UUID;

public record SeedArticleRequest(
        UUID authorId,
        String title,
        String imageUrl,
        boolean published,
        String markdownContent
) {
    public CreateArticleRequest toCreateRequest() {
        return new CreateArticleRequest(title, imageUrl, published, markdownContent);
    }
}
