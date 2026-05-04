package com.articles.services;

import com.articles.dto.ArticleDescriptionResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
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
        ArticleDescriptionResponse response = llmRestClient.post()
                .uri("/api/v1/assistant/description")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .body(draft)
                .retrieve()
                .body(ArticleDescriptionResponse.class);
        return response == null ? null : response.description();
    }

    public void addToVectorStore(ArticleEmbeddingDocument document) {
        llmRestClient.post()
                .uri("/api/v1/vector/articles")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .body(document)
                .retrieve()
                .toBodilessEntity();
    }

    public List<UUID> searchArticleIds(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        RestClient.RequestBodySpec requestSpec = llmRestClient.post()
                .uri("/api/v1/search/articles");

        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        SemanticSearchResponse response = requestSpec
                .body(new SemanticSearchRequest(query, 0.8, 20))
                .retrieve()
                .body(SemanticSearchResponse.class);

        if (response == null || response.articleIds() == null) {
            return List.of();
        }

        return response.articleIds();
    }

    private String bearerToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT token is required for LLM service call");
        }
        return "Bearer " + jwt.getTokenValue();
    }

    private String bearerTokenOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return "Bearer " + jwt.getTokenValue();
    }
}
