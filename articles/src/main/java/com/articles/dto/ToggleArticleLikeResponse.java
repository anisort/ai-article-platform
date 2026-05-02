package com.articles.dto;

import java.util.UUID;

public record ToggleArticleLikeResponse(
        UUID articleId,
        boolean liked
) {
}
