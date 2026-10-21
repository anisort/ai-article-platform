package com.articles.dto.ai;

import com.articles.dto.user.UserBriefResponse;
import com.articles.entities.Article;

import java.util.UUID;

/**
 * Carries article embedding request.
 */
public record ArticleEmbeddingRequest(
        UUID id,
        String content,
        String title,
        String author,
        String image,
        boolean published
) {
/**
 * Returns from article.
 * @param article the article
 * @param author the author
 * @param contentOverride the contentOverride
 * @return the result of the operation
 */
    public static ArticleEmbeddingRequest fromArticle(Article article, UserBriefResponse author, String contentOverride) {
        String authorName = author == null ? "Unknown" : author.name();
        String markdownContent = article.getMarkdownContent() == null ? "" : article.getMarkdownContent();
        String content = contentOverride == null ? markdownContent : contentOverride;
        return new ArticleEmbeddingRequest(
                article.getId(),
                content,
                article.getTitle(),
                authorName,
                article.getImageUrl(),
                article.isPublished()
        );
    }
}

