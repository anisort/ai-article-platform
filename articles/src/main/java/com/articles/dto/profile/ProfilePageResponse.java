package com.articles.dto.profile;

import com.articles.dto.article.ArticlePreviewResponse;

import java.util.List;

public record ProfilePageResponse(
        UserProfileResponse profile,
        List<ArticlePreviewResponse> ownArticles,
        List<ArticlePreviewResponse> likedArticles,
        List<ArticlePreviewResponse> publishedArticles
) {
}

