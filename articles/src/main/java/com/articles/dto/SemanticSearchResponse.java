package com.articles.dto;

import java.util.List;
import java.util.UUID;

public record SemanticSearchResponse(
        List<UUID> articleIds
) {
}
