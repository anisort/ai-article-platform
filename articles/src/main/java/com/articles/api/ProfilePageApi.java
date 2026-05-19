package com.articles.api;

import com.articles.dto.like.ToggleArticleLikeResponse;
import com.articles.dto.profile.ProfilePageResponse;
import com.articles.services.facade.ProfilePageFacadeService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile-page")
public class ProfilePageApi {

    private final ProfilePageFacadeService profilePageFacadeService;

    public ProfilePageApi(ProfilePageFacadeService profilePageFacadeService) {
        this.profilePageFacadeService = profilePageFacadeService;
    }

    @GetMapping("/me")
    public ProfilePageResponse me(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return profilePageFacadeService.getOwnProfilePage(userId, authorizationHeader);
    }

    @GetMapping("/{userId}")
    public ProfilePageResponse byUserId(
            @PathVariable UUID userId,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        return profilePageFacadeService.getForeignProfilePage(userId, authorizationHeader);
    }

    @PostMapping("/articles/{articleId}/like")
    public ToggleArticleLikeResponse toggleLike(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        return profilePageFacadeService.toggleLike(articleId, userId);
    }
}
