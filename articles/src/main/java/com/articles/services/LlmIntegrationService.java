package com.articles.services;

import com.articles.dto.ArticleDescriptionResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
import com.articles.dto.ArticleRecommendationRequest;
import com.articles.dto.ArticleRecommendationResponse;
import com.articles.dto.SemanticSearchRequest;
import com.articles.dto.SemanticSearchResponse;
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

    public String generateDescription(ArticleEmbeddingDraft draft) {
        RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                .uri("/api/v1/assistant/description");

        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        ArticleDescriptionResponse response = requestSpec
                .body(draft)
                .retrieve()
                .body(ArticleDescriptionResponse.class);
        return response == null ? null : response.description();
    }

    public void addToVectorStore(ArticleEmbeddingDocument document) {
        RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                .uri("/api/v1/vector/articles");

        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        requestSpec
                .body(document)
                .retrieve()
                .toBodilessEntity();
    }

    public void addToVectorStoreBatch(List<ArticleEmbeddingDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                .uri("/api/v1/vector/articles/batch");

        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        requestSpec
                .body(documents)
                .retrieve()
                .toBodilessEntity();
    }

    public List<UUID> searchArticleIds(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        List<SemanticSearchRequest> requests = List.of(
                new SemanticSearchRequest(query, 0.8, 30),
                new SemanticSearchRequest(query, 0.65, 40),
                new SemanticSearchRequest(query, 0.5, 50)
        );

        for (SemanticSearchRequest request : requests) {
            RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                    .uri("/api/v1/search/articles");

            String bearerToken = bearerTokenOrNull();
            if (bearerToken != null) {
                requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
            }

            SemanticSearchResponse response = requestSpec
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

        RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                .uri("/api/v1/recommend/articles");

        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        ArticleRecommendationResponse response = requestSpec
                .body(new ArticleRecommendationRequest(likedArticleIds, excludeArticleIds, topK))
                .retrieve()
                .body(ArticleRecommendationResponse.class);

        if (response == null || response.articleIds() == null) {
            return List.of();
        }

        return response.articleIds();
    }

    private String bearerTokenOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return "Bearer " + jwt.getTokenValue();
    }
}
