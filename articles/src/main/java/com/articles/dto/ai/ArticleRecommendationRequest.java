package com.articles.dto.ai;

import java.util.List;
import java.util.UUID;

/**
 * Carries article recommendation request.
 */
public record ArticleRecommendationRequest(
        List<UUID> likedArticleIds,
        List<UUID> excludeArticleIds,
        Integer topK
) {
}
