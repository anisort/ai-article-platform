package com.articles.services.integration;

import com.articles.dto.ai.ArticleDescriptionResponse;
import com.articles.dto.ai.ArticleEmbeddingPayload;
import com.articles.dto.ai.ArticleRecommendationRequest;
import com.articles.dto.ai.ArticleRecommendationResponse;
import com.articles.dto.ai.SemanticSearchRequest;
import com.articles.dto.ai.SemanticSearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
public class LlmIntegrationService {

    private final RestClient llmRestClient;

    public LlmIntegrationService(@Qualifier("llmRestClient") RestClient llmRestClient) {
        this.llmRestClient = llmRestClient;
    }

    public String generateDescription(ArticleEmbeddingPayload payload) {
        ArticleDescriptionResponse response = requestWithAuth("/api/v1/assistant/description")
                .body(payload)
                .retrieve()
                .body(ArticleDescriptionResponse.class);
        return response == null ? null : response.description();
    }

    public void addToVectorStore(ArticleEmbeddingPayload payload) {
        requestWithAuth("/api/v1/vector/articles")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    public List<UUID> searchArticleIds(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        List<SemanticSearchRequest> requests = List.of(
                new SemanticSearchRequest(query, 0.7, 30),
                new SemanticSearchRequest(query, 0.6, 40),
                new SemanticSearchRequest(query, 0.5, 50)
        );

        for (SemanticSearchRequest request : requests) {
            SemanticSearchResponse response = requestWithAuth("/api/v1/discovery/articles/search")
                    .body(request)
                    .retrieve()
                    .body(SemanticSearchResponse.class);

            if (response != null && response.articleIds() != null && !response.articleIds().isEmpty()) {
                return response.articleIds();
            }
        }

        return List.of();
    }

    public List<UUID> recommendArticleIds(List<UUID> likedArticleIds, List<UUID> excludeArticleIds, int topK) {
        if (likedArticleIds == null || likedArticleIds.isEmpty()) {
            return List.of();
        }

        ArticleRecommendationResponse response = requestWithAuth("/api/v1/discovery/articles/recommend")
                .body(new ArticleRecommendationRequest(likedArticleIds, excludeArticleIds, topK))
                .retrieve()
                .body(ArticleRecommendationResponse.class);

        if (response == null || response.articleIds() == null) {
            return List.of();
        }

        return response.articleIds();
    }

    private RestClient.RequestBodySpec requestWithAuth(String uri) {
        RestClient.RequestBodySpec requestSpec = llmRestClient.post().uri(uri);
        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return requestSpec;
    }

    private String bearerTokenOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return "Bearer " + jwt.getTokenValue();
    }
}





