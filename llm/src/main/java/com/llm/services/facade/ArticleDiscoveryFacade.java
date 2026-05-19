package com.llm.services.facade;

import com.llm.dto.discovery.ArticleIdsResponse;
import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.SemanticSearchRequest;
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

    public ArticleIdsResponse searchArticles(SemanticSearchRequest request) {
        double threshold = request.similarityThreshold() == null ? 0.6 : request.similarityThreshold();
        int topK = request.topK() == null ? 20 : request.topK();
        return ArticleIdsResponse.of(
                articleVectorService.similaritySearchArticleIds(request.query(), threshold, topK)
        );
    }

    public ArticleIdsResponse recommendArticles(ArticleRecommendationRequest request) {
        int topK = request.topK() == null ? 20 : request.topK();
        return ArticleIdsResponse.of(
                articleRecommendationService.recommendArticleIds(
                        request.likedArticleIds(),
                        request.excludeArticleIds(),
                        topK
                )
        );
    }
}
