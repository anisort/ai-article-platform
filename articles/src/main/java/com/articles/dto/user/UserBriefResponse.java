package com.articles.dto.user;

import java.io.Serializable;
import java.util.UUID;

/**
 * Carries user brief response.
 */
public record UserBriefResponse(
        UUID id,
        String username,
        String name,
        String avatar
) implements Serializable {
}
