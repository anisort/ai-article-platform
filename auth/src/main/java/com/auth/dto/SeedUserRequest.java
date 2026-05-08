package com.auth.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SeedUserRequest(
        @NotNull UUID id,
        @NotNull String username,
        @NotNull String email,
        @NotNull String name,
        @NotNull String password
) {
}
