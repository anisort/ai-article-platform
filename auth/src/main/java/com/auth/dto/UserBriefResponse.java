package com.auth.dto;

import com.auth.entities.CustomUserDetails;

import java.util.UUID;

public record UserBriefResponse(
        UUID id,
        String username,
        String name,
        String avatar
) {
    public static UserBriefResponse fromEntity(CustomUserDetails user) {
        if (user == null) return null;
        return new UserBriefResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getAvatar()
        );
    }
}
