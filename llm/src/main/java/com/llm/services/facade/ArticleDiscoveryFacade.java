package com.llm.services.facade;

import com.llm.dto.ArticleRecommendationRequest;
import com.llm.dto.ArticleRecommendationResponse;
import com.llm.dto.SemanticSearchRequest;
import com.llm.dto.SemanticSearchResponse;
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
        return new SemanticSearchResponse(
                articleVectorService.similaritySearchArticleIds(request.query(), threshold, topK)
        );
    }

    public ArticleRecommendationResponse recommendArticles(ArticleRecommendationRequest request) {
        int topK = request.topK() == null ? 20 : request.topK();
        return new ArticleRecommendationResponse(
                articleRecommendationService.recommendArticleIds(
                        request.likedArticleIds(),
                        request.excludeArticleIds(),
                        topK
                )
        );
    }
}
