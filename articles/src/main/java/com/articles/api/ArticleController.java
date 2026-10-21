package com.articles.api;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.article.CreateArticleRequest;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.article.EditArticlePageResponse;
import com.articles.services.facade.ArticleFacadeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing article CRUD endpoints under {@code /api/v1/articles}.
 * All requests are delegated to {@link ArticleFacadeService}.
 */
/**
 * Defines article controller.
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    /** Facade handling article retrieval, creation, update and deletion. */
    private final ArticleFacadeService articleFacadeService;

    /**
     * Creates the controller with its article facade dependency.
     *
     * @param articleFacadeService facade used to service article requests
     */
/**
 * Performs article controller.
 * @param articleFacadeService the articleFacadeService
 */
    public ArticleController(ArticleFacadeService articleFacadeService) {
        this.articleFacadeService = articleFacadeService;
    }

    /**
     * Handles {@code GET /api/v1/articles} and returns all published articles
     * via {@link ArticleFacadeService#getPublishedArticles()}.
     *
     * @return the list of published article previews
     */
    @GetMapping
/**
 * Returns get articles.
 * @return the result of the operation
 */
    public List<ArticlePreviewResponse> getArticles() {
        return articleFacadeService.getPublishedArticles();
    }

    /**
     * Handles {@code GET /api/v1/articles/{articleId}} and returns a single
     * article with its author data. When a JWT is present its subject is passed
     * as the requester id; otherwise the requester id is {@code null}.
     *
     * @param articleId the article identifier from the path
     * @param jwt       the authenticated principal, or {@code null} for anonymous requests
     * @return the article together with its user data
     */
    @GetMapping("/{articleId}")
/**
 * Returns get article.
 * @return the result of the operation
 */
    public ArticleWithUserResponse getArticle(@PathVariable UUID articleId, @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return articleFacadeService.getArticle(articleId, requesterId);
    }

    /**
     * Handles {@code GET /api/v1/articles/{articleId}/edit-page} and returns the
     * edit-page data for the article, using the authenticated JWT subject as the
     * requester id.
     *
     * @param articleId the article identifier from the path
     * @param jwt       the authenticated principal whose subject is the requester id
     * @return the edit-page data for the article
     */
    @GetMapping("/{articleId}/edit-page")
/**
 * Returns get edit page.
 * @return the result of the operation
 */
    public EditArticlePageResponse getEditPage(@PathVariable UUID articleId, @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.getEditPage(articleId, requesterId);
    }

    /**
     * Handles {@code POST /api/v1/articles} and creates a new article authored by
     * the authenticated JWT subject.
     *
     * @param jwt the authenticated principal whose subject is the author id
     * @param req the article creation request body
     * @return the created article together with its user data
     */
    @PostMapping
/**
 * Returns create.
 * @return the result of the operation
 */
    public ArticleWithUserResponse create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateArticleRequest req) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.create(req, authorId);
    }

    /**
     * Handles {@code PUT /api/v1/articles/{articleId}} and updates the given
     * article on behalf of the authenticated JWT subject.
     *
     * @param articleId the article identifier from the path
     * @param jwt       the authenticated principal whose subject is the author id
     * @param req       the article update request body
     * @return the updated article together with its user data
     */
    @PutMapping("/{articleId}")
    public ArticleWithUserResponse update(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateArticleRequest req
    ) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.update(articleId, req, authorId);
    }

    /**
     * Handles {@code DELETE /api/v1/articles/{articleId}} and deletes the given
     * article on behalf of the authenticated JWT subject.
     *
     * @param articleId the article identifier from the path
     * @param jwt       the authenticated principal whose subject is the author id
     */
    @DeleteMapping("/{articleId}")
/**
 * Performs delete.
 */
    public void delete(@PathVariable UUID articleId, @AuthenticationPrincipal Jwt jwt) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        articleFacadeService.delete(articleId, authorId);
    }
}
