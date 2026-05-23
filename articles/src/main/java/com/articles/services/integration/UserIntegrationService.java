package com.articles.services.integration;

import com.articles.dto.user.UserBriefResponse;
import com.articles.dto.profile.UserProfileResponse;
import com.articles.entities.Article;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserIntegrationService {

    private final RestClient restClient;

    public UserIntegrationService(@Qualifier("userRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<UserBriefResponse> getUsers(List<UUID> ids) {
        return restClient.post()
                .uri("/api/v1/internal/users/batch")
                .body(ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public Map<UUID, UserBriefResponse> getAuthors(List<Article> articles) {

        List<UUID> authorIds = articles.stream()
                .map(Article::getAuthorId)
                .distinct()
                .toList();

        return getUsers(authorIds).stream()
                .collect(Collectors.toMap(
                        UserBriefResponse::id,
                        author -> author
                ));
    }

    public UserProfileResponse getMyProfile(String authorizationHeader) {
        return restClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(UserProfileResponse.class);
    }

    public UserProfileResponse getProfileById(UUID userId, String authorizationHeader) {
        RestClient.RequestHeadersSpec<?> request = restClient.get()
                .uri("/api/v1/users/{userId}/profile", userId);

        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            request = request.header("Authorization", authorizationHeader);
        }

        try {
            return request.retrieve().body(UserProfileResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
    }
}





