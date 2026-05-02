package com.articles.services;

import com.articles.dto.AuthorResponse;
import com.articles.dto.UserProfilePreviewResponse;
import com.articles.entities.Article;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final RestClient restClient;

    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<AuthorResponse> getUsers(List<UUID> ids) {
        return restClient.post()
                .uri("/api/v1/users/batch")
                .body(ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public Map<UUID, AuthorResponse> getAuthors(List<Article> articles) {

        List<UUID> authorIds = articles.stream()
                .map(Article::getAuthorId)
                .distinct()
                .toList();

        return getUsers(authorIds).stream()
                .collect(Collectors.toMap(
                        AuthorResponse::id,
                        author -> author
                ));
    }

    public UserProfilePreviewResponse getUser(UUID id) {
        return restClient.get()
                .uri("/api/v1/users/{id}", id)
                .retrieve()
                .body(UserProfilePreviewResponse.class);
    }
}
