package com.llm.dto.discovery;

import java.util.List;
import java.util.UUID;

/**
 * Carries article ids response.
 */
public record ArticleIdsResponse(
        List<UUID> articleIds
) {
/**
 * Returns of.
 * @param articleIds the articleIds
 * @return the result of the operation
 */
    public static ArticleIdsResponse of(List<UUID> articleIds) {
        return new ArticleIdsResponse(articleIds);
    }
}
