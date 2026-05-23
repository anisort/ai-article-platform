package com.llm.api;

import com.llm.dto.discovery.ArticleIdsResponse;
import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.SemanticSearchRequest;
import com.llm.services.facade.ArticleDiscoveryFacadeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/discovery")
public class ArticleDiscoveryController {

    private final ArticleDiscoveryFacadeService articleDiscoveryFacadeService;

    public ArticleDiscoveryController(ArticleDiscoveryFacadeService articleDiscoveryFacadeService) {
        this.articleDiscoveryFacadeService = articleDiscoveryFacadeService;
    }

    @PostMapping("/articles/search")
    public ArticleIdsResponse searchArticles(@RequestBody SemanticSearchRequest request) {
        return articleDiscoveryFacadeService.searchArticles(request);
    }

    @PostMapping("/articles/recommend")
    public ArticleIdsResponse recommendArticles(@RequestBody ArticleRecommendationRequest request) {
        return articleDiscoveryFacadeService.recommendArticles(request);
    }
}
