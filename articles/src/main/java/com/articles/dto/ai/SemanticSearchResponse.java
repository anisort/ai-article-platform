package com.articles.dto.ai;

import java.util.List;
import java.util.UUID;

public record SemanticSearchResponse(
        List<UUID> articleIds
) {
}
