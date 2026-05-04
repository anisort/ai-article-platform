package com.articles.api;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.dto.LikedArticlesResponse;
import com.articles.dto.PublishedArticlePreviewResponse;
import com.articles.dto.ToggleArticleLikeResponse;
import com.articles.services.ArticleFacadeService;
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

    @GetMapping("/search")
    public List<PublishedArticlePreviewResponse> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String mode
    ) {
        return articleFacadeService.searchPublishedArticles(query, mode);
    }

    @GetMapping("/me")
    public List<ArticleWithUserResponse> getMyArticles(@AuthenticationPrincipal Jwt jwt) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.getMyArticles(authorId);
    }

    @GetMapping("/author/{authorId}")
    public List<PublishedArticlePreviewResponse> getPublishedArticlesByAuthor(@PathVariable UUID authorId) {
        return articleFacadeService.getPublishedArticlesByAuthor(authorId);
    }

    @GetMapping("/me/likes")
    public LikedArticlesResponse getMyLikedArticles(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return articleFacadeService.getLikedArticles(userId);
    }

    @GetMapping("/{articleId}")
    public ArticleWithUserResponse getArticle(@PathVariable UUID articleId, @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return articleFacadeService.getArticle(articleId, requesterId);
    }

    @PostMapping
    public ArticleWithUserResponse create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateArticleRequest req) {
        UUID authorId = UUID.fromString(jwt.getClaimAsString("sub"));
        return articleFacadeService.create(req, authorId);
    }

    @PutMapping("/{articleId}")
    public ArticleWithUserResponse update(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateArticleRequest req
    ) {
        UUID authorId = UUID.fromString(jwt.getClaimAsString("sub"));
        return articleFacadeService.update(articleId, req, authorId);
    }

    @PostMapping("/{articleId}/like")
    public ToggleArticleLikeResponse toggleLike(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        return articleFacadeService.toggleLike(articleId, userId);
    }
}
