package com.articles.services.integration;

import com.articles.dto.ai.ArticleDescriptionResponse;
import com.articles.dto.ai.ArticleEmbeddingRequest;
import com.articles.dto.ai.ArticleRecommendationRequest;
import com.articles.dto.ai.ArticleRecommendationResponse;
import com.articles.dto.ai.SemanticSearchRequest;
import com.articles.dto.ai.SemanticSearchResponse;
import com.articles.dto.assistant.ArticleAssistantChatSummary;
import com.articles.dto.assistant.ArticleAssistantMessageSummary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Defines llm integration service.
 */
@Service
public class LlmIntegrationService {

    private final RestClient llmRestClient;

/**
 * Performs llm integration service.
 * @return the result of the operation
 */
    public LlmIntegrationService(@Qualifier("llmRestClient") RestClient llmRestClient) {
        this.llmRestClient = llmRestClient;
    }

/**
 * Returns generate description.
 * @param payload the payload
 * @return the result of the operation
 */
    public String generateDescription(ArticleEmbeddingRequest payload) {
        ArticleDescriptionResponse response = requestWithAuth("/api/v1/assistant/description")
                .body(payload)
                .retrieve()
                .body(ArticleDescriptionResponse.class);
        return response == null ? null : response.description();
    }

/**
 * Performs add to vector store.
 * @param payload the payload
 */
    public void addToVectorStore(ArticleEmbeddingRequest payload) {
        requestWithAuth("/api/v1/vector/articles")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

/**
 * Performs delete from vector store.
 * @param articleId the articleId
 */
    public void deleteFromVectorStore(UUID articleId) {
        RestClient.RequestHeadersSpec<?> requestSpec = llmRestClient.delete()
                .uri("/api/v1/vector/articles/{articleId}", articleId);
        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        requestSpec.retrieve().toBodilessEntity();
    }

/**
 * Returns search article ids.
 * @param query the query
 * @return the result of the operation
 */
    public List<UUID> searchArticleIds(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        List<SemanticSearchRequest> requests = List.of(
                new SemanticSearchRequest(query, 0.9, 30),
                new SemanticSearchRequest(query, 0.8, 40),
                new SemanticSearchRequest(query, 0.7, 50)
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

/**
 * Returns recommend article ids.
 * @param likedArticleIds the likedArticleIds
 * @param excludeArticleIds the excludeArticleIds
 * @param topK the topK
 * @return the result of the operation
 */
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

/**
 * Returns list article chats.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public List<ArticleAssistantChatSummary> listArticleChats(UUID articleId) {
        RestClient.RequestHeadersSpec<?> requestSpec = llmRestClient.get()
                .uri("/api/v1/assistant/articles/{articleId}/chats", articleId);
        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec = requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return requestSpec.retrieve().body(new ParameterizedTypeReference<>() {});
    }

/**
 * Returns list article chat messages.
 * @param articleId the articleId
 * @param chatId the chatId
 * @return the result of the operation
 */
    public List<ArticleAssistantMessageSummary> listArticleChatMessages(UUID articleId, UUID chatId) {
        RestClient.RequestHeadersSpec<?> requestSpec = llmRestClient.get()
                .uri("/api/v1/assistant/articles/{articleId}/chats/{chatId}/messages", articleId, chatId);
        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec = requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return requestSpec.retrieve().body(new ParameterizedTypeReference<>() {});
    }

/**
 * Returns request with auth.
 * @param uri the uri
 * @return the result of the operation
 */
    private RestClient.RequestBodySpec requestWithAuth(String uri) {
        RestClient.RequestBodySpec requestSpec = llmRestClient.post().uri(uri);
        String bearerToken = bearerTokenOrNull();
        if (bearerToken != null) {
            requestSpec.header(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return requestSpec;
    }

/**
 * Returns bearer token or null.
 * @return the result of the operation
 */
    private String bearerTokenOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return "Bearer " + jwt.getTokenValue();
    }
}






