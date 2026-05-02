package com.auth.api;

import com.auth.dto.CreateUserRequest;
import com.auth.dto.ToggleFollowResponse;
import com.auth.dto.UserProfileResponse;
import com.auth.dto.UserResponse;
import com.auth.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse save(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.fromEntity(userService.save(request));
    }

    @PostMapping("/batch")
    public List<UserResponse> batch(@RequestBody List<UUID> ids) {
        return userService.getByIds(ids).stream().map(UserResponse::fromEntity).toList();
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable UUID userId) {
        return UserResponse.fromEntity(userService.getById(userId));
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return userService.getMe(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{userId}/profile")
    public UserProfileResponse profile(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        UUID viewerId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return userService.getProfile(userId, viewerId);
    }

    @PostMapping("/{userId}/follow")
    public ToggleFollowResponse toggleFollow(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        return userService.toggleFollow(UUID.fromString(jwt.getSubject()), userId);
    }
}
