package com.articles.api;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.article.CreateArticleRequest;
import com.articles.dto.article.PublishedArticlePreviewResponse;
import com.articles.services.facade.ArticleFacadeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleApi {

    private final ArticleFacadeService articleFacadeService;

    public ArticleApi(ArticleFacadeService articleFacadeService) {
        this.articleFacadeService = articleFacadeService;
    }

    @GetMapping
    public List<PublishedArticlePreviewResponse> getArticles() {
        return articleFacadeService.getPublishedArticles();
    }

    @GetMapping("/author/{authorId}")
    public List<PublishedArticlePreviewResponse> getPublishedArticlesByAuthor(@PathVariable UUID authorId) {
        return articleFacadeService.getPublishedArticlesByAuthor(authorId);
    }

    @GetMapping("/{articleId}")
    public ArticleWithUserResponse getArticle(@PathVariable UUID articleId, @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return articleFacadeService.getArticle(articleId, requesterId);
    }

    @PostMapping
    public ArticleWithUserResponse create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateArticleRequest req) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.create(req, authorId);
    }

    @PutMapping("/{articleId}")
    public ArticleWithUserResponse update(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateArticleRequest req
    ) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.update(articleId, req, authorId);
    }
}
