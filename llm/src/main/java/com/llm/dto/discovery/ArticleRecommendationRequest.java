package com.llm.dto.discovery;

import java.util.List;
import java.util.UUID;

public record ArticleRecommendationRequest(
        List<UUID> likedArticleIds,
        List<UUID> excludeArticleIds,
        Integer topK
) {
}
