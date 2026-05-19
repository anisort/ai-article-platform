package com.articles.dto.article;

import com.articles.entities.Article;
import com.articles.dto.user.AuthorResponse;

import java.io.Serializable;

public record ArticleWithUserResponse(
        ArticleResponse articleResponse,
        AuthorResponse authorResponse
) implements Serializable {
    public static ArticleWithUserResponse mapToResponse(
            Article article,
            AuthorResponse authorResponse,
            long likesCount,
            boolean likedByRequester
    ) {
        return new ArticleWithUserResponse(
                ArticleResponse.fromEntity(article, likesCount, likedByRequester),
                authorResponse
        );
    }
}
