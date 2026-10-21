package com.llm.services.facade;

import com.llm.dto.discovery.ArticleIdsResponse;
import com.llm.dto.discovery.ArticleRecommendationRequest;
import com.llm.dto.discovery.SemanticSearchRequest;
import com.llm.services.llm.ArticleRecommendationService;
import com.llm.services.llm.ArticleVectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Defines article discovery facade service.
 */
@Service
public class ArticleDiscoveryFacadeService {

    private static final Logger log = LoggerFactory.getLogger(ArticleDiscoveryFacadeService.class);

    private final ArticleVectorService articleVectorService;
    private final ArticleRecommendationService articleRecommendationService;

    public ArticleDiscoveryFacadeService(
            ArticleVectorService articleVectorService,
            ArticleRecommendationService articleRecommendationService
    ) {
        this.articleVectorService = articleVectorService;
        this.articleRecommendationService = articleRecommendationService;
    }

/**
 * Returns search articles.
 * @param request the request
 * @return the result of the operation
 */
    public ArticleIdsResponse searchArticles(SemanticSearchRequest request) {
        double threshold = request.similarityThreshold() == null ? 0.6 : request.similarityThreshold();
        int topK = request.topK() == null ? 20 : request.topK();
        try {
            return ArticleIdsResponse.of(
                    articleVectorService.similaritySearchArticleIds(request.query(), threshold, topK)
            );
        } catch (Exception ex) {
            log.error(
                    "Semantic search failed in ArticleDiscoveryFacadeService. query='{}', threshold={}, topK={}",
                    request.query(),
                    threshold,
                    topK,
                    ex
            );
            throw ex;
        }
    }

/**
 * Returns recommend articles.
 * @param request the request
 * @return the result of the operation
 */
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
