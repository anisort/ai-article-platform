package com.articles.dto.like;

import java.util.UUID;

/**
 * Carries toggle article like response.
 */
public record ToggleArticleLikeResponse(
        UUID articleId,
        boolean liked,
        long likesCount
) {
}
