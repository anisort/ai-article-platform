package com.articles.api;

import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.services.facade.ArticleRecommendationFacadeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Defines article recommendation controller.
 */
@RestController
@RequestMapping("/api/v1/articles/recommendations")
public class ArticleRecommendationController {

    private final ArticleRecommendationFacadeService articleRecommendationFacadeService;

/**
 * Performs article recommendation controller.
 * @param articleRecommendationFacadeService the articleRecommendationFacadeService
 */
    public ArticleRecommendationController(ArticleRecommendationFacadeService articleRecommendationFacadeService) {
        this.articleRecommendationFacadeService = articleRecommendationFacadeService;
    }

    @GetMapping
    public List<ArticlePreviewResponse> getRecommendations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "20") int limit
    ) {
        UUID userId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return articleRecommendationFacadeService.getRecommendations(userId, limit);
    }
}
