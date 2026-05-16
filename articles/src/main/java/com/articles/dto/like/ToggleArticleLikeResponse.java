package com.articles.dto.like;

import java.util.UUID;

public record ToggleArticleLikeResponse(
        UUID articleId,
        boolean liked,
        long likesCount
) {
}
