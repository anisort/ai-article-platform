package com.articles.services;

import com.articles.dto.ArticleDescriptionResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    private String bearerToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT token is required for LLM service call");
        }
        return "Bearer " + jwt.getTokenValue();
    }
}
