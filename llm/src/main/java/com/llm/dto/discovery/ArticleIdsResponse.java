package com.llm.dto.discovery;

import java.util.List;
import java.util.UUID;

public record ArticleIdsResponse(
        List<UUID> articleIds
) {
    public static ArticleIdsResponse of(List<UUID> articleIds) {
        return new ArticleIdsResponse(articleIds);
    }
}
