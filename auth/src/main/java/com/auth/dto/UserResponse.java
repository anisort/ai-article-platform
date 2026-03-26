package com.auth.dto;

import com.auth.entities.CustomUserDetails;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserResponse(
        @NotNull UUID id,
        @NotNull String username,
        @NotNull String email,
        @NotNull String name
        ) {
    public static UserResponse fromEntity(CustomUserDetails user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName());
    }
}
