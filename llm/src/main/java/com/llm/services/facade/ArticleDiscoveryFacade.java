package com.llm.services.facade;

import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.ArticleRecommendationResponse;
import com.llm.dto.discovery.SemanticSearchRequest;
import com.llm.dto.discovery.SemanticSearchResponse;
import com.llm.services.llm.ArticleRecommendationService;
import com.llm.services.llm.ArticleVectorService;
import org.springframework.stereotype.Service;

@Service
public class ArticleDiscoveryFacade {

    private final ArticleVectorService articleVectorService;
    private final ArticleRecommendationService articleRecommendationService;

    public ArticleDiscoveryFacade(
            ArticleVectorService articleVectorService,
            ArticleRecommendationService articleRecommendationService
    ) {
        this.articleVectorService = articleVectorService;
        this.articleRecommendationService = articleRecommendationService;
    }

    public SemanticSearchResponse searchArticles(SemanticSearchRequest request) {
        double threshold = request.similarityThreshold() == null ? 0.6 : request.similarityThreshold();
        int topK = request.topK() == null ? 20 : request.topK();
        return SemanticSearchResponse.of(
                articleVectorService.similaritySearchArticleIds(request.query(), threshold, topK)
        );
    }

    public ArticleRecommendationResponse recommendArticles(ArticleRecommendationRequest request) {
        int topK = request.topK() == null ? 20 : request.topK();
        return ArticleRecommendationResponse.of(
                articleRecommendationService.recommendArticleIds(
                        request.likedArticleIds(),
                        request.excludeArticleIds(),
                        topK
                )
        );
    }
}
