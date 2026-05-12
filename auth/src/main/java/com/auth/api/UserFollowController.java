package com.auth.api;

import com.auth.dto.ToggleFollowResponse;
import com.auth.services.UserFollowService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @PostMapping("/{userId}/follow")
    public ToggleFollowResponse toggleFollow(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        return userFollowService.toggleFollow(UUID.fromString(jwt.getSubject()), userId);
    }
}
