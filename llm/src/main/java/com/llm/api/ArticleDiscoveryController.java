package com.llm.api;

import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.ArticleRecommendationResponse;
import com.llm.dto.discovery.SemanticSearchRequest;
import com.llm.dto.discovery.SemanticSearchResponse;
import com.llm.services.facade.ArticleDiscoveryFacade;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/discovery")
public class ArticleDiscoveryController {

    private final ArticleDiscoveryFacade articleDiscoveryFacade;

    public ArticleDiscoveryController(ArticleDiscoveryFacade articleDiscoveryFacade) {
        this.articleDiscoveryFacade = articleDiscoveryFacade;
    }

    @PostMapping("/articles/search")
    public SemanticSearchResponse searchArticles(@RequestBody SemanticSearchRequest request) {
        return articleDiscoveryFacade.searchArticles(request);
    }

    @PostMapping("/articles/recommend")
    public ArticleRecommendationResponse recommendArticles(@RequestBody ArticleRecommendationRequest request) {
        return articleDiscoveryFacade.recommendArticles(request);
    }
}
