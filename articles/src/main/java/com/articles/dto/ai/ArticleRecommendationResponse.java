package com.articles.dto.ai;

import java.util.List;
import java.util.UUID;

/**
 * Carries article recommendation response.
 */
public record ArticleRecommendationResponse(
        List<UUID> articleIds
) {
}
