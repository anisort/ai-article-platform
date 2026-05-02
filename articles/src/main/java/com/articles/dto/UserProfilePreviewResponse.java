package com.articles.dto;

import java.util.UUID;

public record UserProfilePreviewResponse(
        UUID id,
        String username,
        String email,
        String name,
        String avatar
) {
}
