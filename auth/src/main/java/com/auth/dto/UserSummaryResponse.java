package com.auth.dto;

import com.auth.entities.CustomUserDetails;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String name,
        String avatar
) {
    public static UserSummaryResponse fromEntity(CustomUserDetails user) {
        if (user == null) return null;
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getAvatar()
        );
    }
}
