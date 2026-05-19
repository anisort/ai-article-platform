package com.auth.dto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String name,
        String email,
        String avatar,
        boolean ownProfile,
        boolean following,
        List<UserBriefResponse> followers,
        List<UserBriefResponse> followingUsers
) {
}
