package com.articles.dto.article;

import com.articles.entities.Article;

import java.util.UUID;

/**
 * Carries create article request.
 */
public record CreateArticleRequest(
        String title,
        String imageUrl,
        boolean published,
        String markdownContent
) {
/**
 * Returns to new article.
 * @param authorId the authorId
 * @return the result of the operation
 */
    public Article toNewArticle(UUID authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        applyTo(article);
        return article;
    }

/**
 * Performs apply to.
 * @param article the article
 */
    public void applyTo(Article article) {
        article.setTitle(title);
        article.setImageUrl(imageUrl);
        article.setPublished(published);
        article.setSummary(null);
        article.setMarkdownContent(markdownContent == null ? "" : markdownContent);
    }
}
