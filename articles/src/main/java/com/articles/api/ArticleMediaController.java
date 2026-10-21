package com.articles.api;

import com.articles.dto.image.AddArticleImageRequest;
import com.articles.dto.image.ArticleImageResponse;
import com.articles.services.facade.ArticleMediaFacadeService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Defines article media controller.
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleMediaController {

    private final ArticleMediaFacadeService articleMediaFacadeService;

/**
 * Performs article media controller.
 * @param articleMediaFacadeService the articleMediaFacadeService
 */
    public ArticleMediaController(ArticleMediaFacadeService articleMediaFacadeService) {
        this.articleMediaFacadeService = articleMediaFacadeService;
    }

    @PostMapping("/{articleId}/images")
    public ArticleImageResponse addArticleImage(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddArticleImageRequest request
    ) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        return articleMediaFacadeService.addArticleImage(articleId, requesterId, request.imageUrl());
    }

    @DeleteMapping("/{articleId}/images/{imageId}")
    public void deleteArticleImage(
            @PathVariable UUID articleId,
            @PathVariable UUID imageId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        articleMediaFacadeService.deleteArticleImage(articleId, imageId, requesterId, authorizationHeader);
    }
}
