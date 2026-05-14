package com.articles.dto.profile;

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
        List<UserSummaryProfileResponse> followers,
        List<UserSummaryProfileResponse> followingUsers
) {
}
