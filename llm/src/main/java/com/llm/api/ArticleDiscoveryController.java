package com.llm.api;

import com.llm.dto.ArticleRecommendationRequest;
import com.llm.dto.ArticleRecommendationResponse;
import com.llm.dto.SemanticSearchRequest;
import com.llm.dto.SemanticSearchResponse;
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
