package com.articles.dto.profile;

import com.articles.dto.user.UserBriefResponse;

import java.util.List;
import java.util.UUID;

/**
 * Carries user profile response.
 */
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
