package com.articles.dto.profile;

import java.util.UUID;

public record UserSummaryProfileResponse(
        UUID id,
        String username,
        String name,
        String avatar
) {
}
