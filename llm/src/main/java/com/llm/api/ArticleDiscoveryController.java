package com.llm.api;

import com.llm.dto.discovery.ArticleIdsResponse;
import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.SemanticSearchRequest;
import com.llm.services.facade.ArticleDiscoveryFacadeService;
import org.springframework.web.bind.annotation.*;

/**
 * Defines article discovery controller.
 */
@RestController
@RequestMapping("/api/v1/discovery")
public class ArticleDiscoveryController {

    private final ArticleDiscoveryFacadeService articleDiscoveryFacadeService;

/**
 * Performs article discovery controller.
 * @param articleDiscoveryFacadeService the articleDiscoveryFacadeService
 */
    public ArticleDiscoveryController(ArticleDiscoveryFacadeService articleDiscoveryFacadeService) {
        this.articleDiscoveryFacadeService = articleDiscoveryFacadeService;
    }

    @PostMapping("/articles/search")
/**
 * Returns search articles.
 * @return the result of the operation
 */
    public ArticleIdsResponse searchArticles(@RequestBody SemanticSearchRequest request) {
        return articleDiscoveryFacadeService.searchArticles(request);
    }

    @PostMapping("/articles/recommend")
/**
 * Returns recommend articles.
 * @return the result of the operation
 */
    public ArticleIdsResponse recommendArticles(@RequestBody ArticleRecommendationRequest request) {
        return articleDiscoveryFacadeService.recommendArticles(request);
    }
}
