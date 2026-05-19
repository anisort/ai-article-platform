package com.articles.dto.article;

import com.articles.entities.Article;
import com.articles.dto.user.UserBriefResponse;

import java.io.Serializable;

public record ArticleWithUserResponse(
        ArticleResponse articleResponse,
        UserBriefResponse UserBriefResponse
) implements Serializable {
    public static ArticleWithUserResponse mapToResponse(
            Article article,
            UserBriefResponse UserBriefResponse,
            long likesCount,
            boolean likedByRequester
    ) {
        return new ArticleWithUserResponse(
                ArticleResponse.fromEntity(article, likesCount, likedByRequester),
                UserBriefResponse
        );
    }
}
