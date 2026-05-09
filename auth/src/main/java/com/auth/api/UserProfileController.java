package com.auth.api;

import com.auth.dto.UserProfileResponse;
import com.auth.services.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return userProfileService.getMe(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{userId}/profile")
    public UserProfileResponse profile(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        UUID viewerId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return userProfileService.getProfile(userId, viewerId);
    }
}
