package com.articles.dto.article;

import com.articles.entities.Article;
import com.articles.dto.user.UserBriefResponse;

import java.io.Serializable;

/**
 * Carries article with user response.
 */
public record ArticleWithUserResponse(
        ArticleResponse articleResponse,
        UserBriefResponse authorResponse
) implements Serializable {
    public static ArticleWithUserResponse mapToResponse(
            Article article,
            UserBriefResponse authorResponse,
            long likesCount,
            boolean likedByRequester
    ) {
        return new ArticleWithUserResponse(
                ArticleResponse.fromEntity(article, likesCount, likedByRequester),
                authorResponse
        );
    }
}
