package com.llm.dto.discovery;

import java.util.List;
import java.util.UUID;

public record SemanticSearchResponse(
        List<UUID> articleIds
) {
    public static SemanticSearchResponse of(List<UUID> articleIds) {
        return new SemanticSearchResponse(articleIds);
    }
}
