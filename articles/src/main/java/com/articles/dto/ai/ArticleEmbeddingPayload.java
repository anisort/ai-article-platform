package com.articles.dto.ai;

import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;

import java.util.UUID;

public record ArticleEmbeddingPayload(
        UUID id,
        String content,
        String title,
        String author,
        String image
) {
    public static ArticleEmbeddingPayload fromArticle(Article article, AuthorResponse author, String contentOverride) {
        String authorName = author == null ? "Unknown" : author.name();
        String markdownContent = article.getMarkdownContent() == null ? "" : article.getMarkdownContent();
        String content = contentOverride == null ? markdownContent : contentOverride;
        return new ArticleEmbeddingPayload(
                article.getId(),
                content,
                article.getTitle(),
                authorName,
                article.getImageUrl()
        );
    }
}
