package com.llm.dto;

import java.util.List;
import java.util.UUID;

public record ArticleRecommendationResponse(
        List<UUID> articleIds
) {
}
