package com.articles.services.core;

import com.articles.dto.article.CreateArticleRequest;
import com.articles.entities.Article;
import com.articles.repositories.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Read model: published article queries
    public List<Article> getPublishedArticles() {
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Article> searchPublishedArticlesByTitle(String query) {
        return articleRepository.findByPublishedTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(query);
    }

    public List<Article> getPublishedArticlesByIds(List<UUID> ids) {
        return articleRepository.findByIdInAndPublishedTrue(ids);
    }

    // Read model: author-scoped queries
    public List<Article> getArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    public List<Article> getPublishedArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(authorId);
    }

    public Optional<Article> getById(UUID articleId) {
        return articleRepository.findById(articleId);
    }

    // Access policy: guard methods for application/facade layer
    public Article requirePublished(UUID articleId) {
        Article article = requireById(articleId);
        if (!article.isPublished()) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return article;
    }

    public Article requireAccessible(UUID articleId, UUID requesterId) {
        Article article = requireById(articleId);
        if (article.isPublished() || article.getAuthorId().equals(requesterId)) {
            return article;
        }
        throw new ResponseStatusException(NOT_FOUND);
    }

    public Article requireOwned(UUID articleId, UUID requesterId) {
        Article article = requireById(articleId);
        if (!article.getAuthorId().equals(requesterId)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return article;
    }

    // Write model: state-changing operations
    @Transactional
    public Article create(CreateArticleRequest request, UUID authorId) {
        Article article = request.toNewArticle(authorId);
        return articleRepository.save(article);
    }

    @Transactional
    public Article update(Article article, CreateArticleRequest request) {
        request.applyTo(article);
        return articleRepository.save(article);
    }

    @Transactional
    public void delete(Article article) {
        articleRepository.delete(article);
    }

    // Access helper used by guard methods
    private Article requireById(UUID articleId) {
        return getById(articleId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

}
