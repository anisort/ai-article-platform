package com.auth.dto;

import java.util.UUID;

public record ToggleFollowResponse(
        UUID targetUserId,
        boolean following
) {
}
