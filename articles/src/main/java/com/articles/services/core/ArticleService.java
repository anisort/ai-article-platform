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

/**
 * Core domain service for {@link Article} entities.
 *
 * <p>Provides read queries over the article store and state-changing (write) operations,
 * as well as access-policy guard methods used by the facade/application layer. All
 * persistence is delegated to {@link ArticleRepository}.</p>
 */
/**
 * Defines article service.
 */
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * Creates the service with its backing repository.
     *
     * @param articleRepository repository used for all article persistence and queries
     */
/**
 * Performs article service.
 * @param articleRepository the articleRepository
 */
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Read model: published article queries

    /**
     * Returns all published articles ordered by creation date, newest first.
     *
     * @return the list of published articles
     */
/**
 * Returns get published articles.
 * @return the result of the operation
 */
    public List<Article> getPublishedArticles() {
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    /**
     * Returns published articles whose title contains the given query (case-insensitive),
     * ordered by creation date, newest first.
     *
     * @param query the title substring to match
     * @return the matching published articles
     */
/**
 * Returns search published articles by title.
 * @param query the query
 * @return the result of the operation
 */
    public List<Article> searchPublishedArticlesByTitle(String query) {
        return articleRepository.findByPublishedTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(query);
    }

    /**
     * Returns the published articles whose identifiers are contained in the given list.
     *
     * @param ids the article identifiers to look up
     * @return the matching published articles
     */
/**
 * Returns get published articles by ids.
 * @param ids the ids
 * @return the result of the operation
 */
    public List<Article> getPublishedArticlesByIds(List<UUID> ids) {
        return articleRepository.findByIdInAndPublishedTrue(ids);
    }

    // Read model: author-scoped queries

    /**
     * Returns all articles authored by the given user (published and unpublished),
     * ordered by creation date, newest first.
     *
     * @param authorId the author identifier
     * @return the author's articles
     */
/**
 * Returns get articles by author.
 * @param authorId the authorId
 * @return the result of the operation
 */
    public List<Article> getArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    /**
     * Returns the published articles authored by the given user, ordered by creation
     * date, newest first.
     *
     * @param authorId the author identifier
     * @return the author's published articles
     */
/**
 * Returns get published articles by author.
 * @param authorId the authorId
 * @return the result of the operation
 */
    public List<Article> getPublishedArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(authorId);
    }

    /**
     * Looks up a single article by its identifier.
     *
     * @param articleId the article identifier
     * @return an {@link Optional} containing the article if found, otherwise empty
     */
/**
 * Returns get by id.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public Optional<Article> getById(UUID articleId) {
        return articleRepository.findById(articleId);
    }

    // Access policy: guard methods for application/facade layer

    /**
     * Returns the article with the given identifier, requiring it to be published.
     *
     * @param articleId the article identifier
     * @return the published article
     * @throws ResponseStatusException with {@code 404 NOT_FOUND} if the article does not
     *         exist or is not published
     */
/**
 * Returns require published.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public Article requirePublished(UUID articleId) {
        Article article = requireById(articleId);
        if (!article.isPublished()) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return article;
    }

/**
 * Returns require accessible.
 * @param articleId the articleId
 * @param requesterId the requesterId
 * @return the result of the operation
 */
    public Article requireAccessible(UUID articleId, UUID requesterId) {
        Article article = requireById(articleId);
        if (article.isPublished() || article.getAuthorId().equals(requesterId)) {
            return article;
        }
        throw new ResponseStatusException(NOT_FOUND);
    }

/**
 * Returns require owned.
 * @param articleId the articleId
 * @param requesterId the requesterId
 * @return the result of the operation
 */
    public Article requireOwned(UUID articleId, UUID requesterId) {
        Article article = requireById(articleId);
        if (!article.getAuthorId().equals(requesterId)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return article;
    }

    // Write model: state-changing operations
    @Transactional
/**
 * Returns create.
 * @param request the request
 * @param authorId the authorId
 * @return the result of the operation
 */
    public Article create(CreateArticleRequest request, UUID authorId) {
        Article article = request.toNewArticle(authorId);
        return articleRepository.save(article);
    }

    @Transactional
/**
 * Returns update.
 * @param article the article
 * @param request the request
 * @return the result of the operation
 */
    public Article update(Article article, CreateArticleRequest request) {
        request.applyTo(article);
        return articleRepository.save(article);
    }

    @Transactional
/**
 * Performs delete.
 * @param article the article
 */
    public void delete(Article article) {
        articleRepository.delete(article);
    }

    // Access helper used by guard methods
/**
 * Returns require by id.
 * @param articleId the articleId
 * @return the result of the operation
 */
    private Article requireById(UUID articleId) {
        return getById(articleId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

}
