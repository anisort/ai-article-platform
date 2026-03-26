package com.articles.dto;

import com.articles.entities.Article;

public record ArticleWithUserResponse(
        ArticleResponse articleResponse,
        AuthorResponse authorResponse
) {
    public static ArticleWithUserResponse mapToResponse(Article article, AuthorResponse authorResponse) {
        return new ArticleWithUserResponse(
                ArticleResponse.fromEntity(article),
                authorResponse
        );
    }
}
