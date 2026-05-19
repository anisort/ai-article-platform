package com.auth.api;

import com.auth.dto.ToggleFollowResponse;
import com.auth.dto.UserProfileResponse;
import com.auth.services.UserProfileService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(value = "/me/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponse updateMe(@AuthenticationPrincipal Jwt jwt,
                                        @RequestPart("username") String username,
                                        @RequestPart("name") String name,
                                        @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return userProfileService.updateMe(UUID.fromString(jwt.getSubject()), username, name, avatar);
    }

    @PostMapping("/{userId}/follow")
    public ToggleFollowResponse toggleFollow(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        return userProfileService.toggleFollow(UUID.fromString(jwt.getSubject()), userId);
    }
}
