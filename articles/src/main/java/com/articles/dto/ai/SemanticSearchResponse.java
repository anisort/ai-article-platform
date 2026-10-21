package com.articles.dto.ai;

import java.util.List;
import java.util.UUID;

/**
 * Carries semantic search response.
 */
public record SemanticSearchResponse(
        List<UUID> articleIds
) {
}
