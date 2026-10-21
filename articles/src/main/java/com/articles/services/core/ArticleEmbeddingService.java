package com.articles.services.core;

import com.articles.dto.ai.ArticleEmbeddingRequest;
import com.articles.dto.user.UserBriefResponse;
import com.articles.entities.Article;
import com.articles.services.integration.LlmIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Defines article embedding service.
 */
@Service
public class ArticleEmbeddingService {
    private static final Logger log = LoggerFactory.getLogger(ArticleEmbeddingService.class);

    private final LlmIntegrationService llmIntegrationService;

/**
 * Performs article embedding service.
 * @param llmIntegrationService the llmIntegrationService
 */
    public ArticleEmbeddingService(LlmIntegrationService llmIntegrationService) {
        this.llmIntegrationService = llmIntegrationService;
    }

/**
 * Performs sync article embedding.
 * @param article the article
 * @param author the author
 */
    public void syncArticleEmbedding(Article article, UserBriefResponse author) {
        ArticleEmbeddingRequest payload = ArticleEmbeddingRequest.fromArticle(article, author, null);
        String description = loadDescription(payload, article.getId());
        syncVector(ArticleEmbeddingRequest.fromArticle(article, author, description), article.getId());
    }

/**
 * Performs delete article embedding.
 * @param articleId the articleId
 */
    public void deleteArticleEmbedding(java.util.UUID articleId) {
        try {
            llmIntegrationService.deleteFromVectorStore(articleId);
        } catch (Exception ex) {
            log.warn("Vector store delete failed for article {}. Continue with article removal.", articleId);
        }
    }

/**
 * Returns load description.
 * @param payload the payload
 * @param articleId the articleId
 * @return the result of the operation
 */
    private String loadDescription(ArticleEmbeddingRequest payload, java.util.UUID articleId) {
        String description;
        try {
            description = llmIntegrationService.generateDescription(payload);
        } catch (Exception ex) {
            log.warn("LLM description generation failed for article {}. Fallback to local summary text.", articleId);
            description = payload.content();
        }

        if (!StringUtils.hasText(description)) {
            description = StringUtils.hasText(payload.content())
                    ? payload.content()
                    : (payload.title() == null ? "Programming article" : payload.title());
        }
        return description;
    }

/**
 * Performs sync vector.
 * @param payload the payload
 * @param articleId the articleId
 */
    private void syncVector(ArticleEmbeddingRequest payload, java.util.UUID articleId) {
        try {
            llmIntegrationService.addToVectorStore(payload);
        } catch (Exception ex) {
            log.warn("Vector store sync failed for article {}. Continue without embedding.", articleId);
        }
    }
}

