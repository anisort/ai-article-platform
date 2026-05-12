package com.llm.dto.discovery;

import java.util.List;
import java.util.UUID;

public record ArticleRecommendationResponse(
        List<UUID> articleIds
) {
    public static ArticleRecommendationResponse of(List<UUID> articleIds) {
        return new ArticleRecommendationResponse(articleIds);
    }
}
