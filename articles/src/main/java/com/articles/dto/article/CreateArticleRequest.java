package com.articles.dto.article;

import com.articles.entities.Article;

import java.util.UUID;

public record CreateArticleRequest(
        String title,
        String imageUrl,
        boolean published,
        String markdownContent
) {
    public Article toNewArticle(UUID authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        applyTo(article);
        return article;
    }

    public void applyTo(Article article) {
        article.setTitle(title);
        article.setImageUrl(imageUrl);
        article.setPublished(published);
        article.setSummary(null);
        article.setMarkdownContent(markdownContent == null ? "" : markdownContent);
    }
}
