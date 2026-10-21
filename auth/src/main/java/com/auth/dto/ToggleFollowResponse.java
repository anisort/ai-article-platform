package com.auth.dto;

import java.util.UUID;

/**
 * Carries toggle follow response.
 */
public record ToggleFollowResponse(
        UUID targetUserId,
        boolean following
) {
}
