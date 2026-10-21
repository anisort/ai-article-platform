package com.auth.dto;

import com.auth.entities.CustomUserDetails;
import com.auth.entities.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Carries create user request.
 */
public record CreateUserRequest(
        @NotNull String username,
        @NotNull String email,
        @NotNull String name,
        @NotNull String password,
        @NotNull String repeatPassword
) {
/**
 * Performs to user.
 * @param user the user
 */
    public void toUser(CustomUserDetails user) {
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(Role.USER);
    }
}
