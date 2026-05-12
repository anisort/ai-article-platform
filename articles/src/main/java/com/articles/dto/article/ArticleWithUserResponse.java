package com.articles.dto.article;

import com.articles.entities.Article;
import com.articles.dto.user.AuthorResponse;

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
