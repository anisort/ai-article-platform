package com.articles.services.core;

import com.articles.dto.ai.ArticleEmbeddingPayload;
import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;
import com.articles.services.integration.LlmIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ArticleEmbeddingService {
    private static final Logger log = LoggerFactory.getLogger(ArticleEmbeddingService.class);

    private final LlmIntegrationService llmIntegrationService;

    public ArticleEmbeddingService(LlmIntegrationService llmIntegrationService) {
        this.llmIntegrationService = llmIntegrationService;
    }

    public void syncArticleEmbedding(Article article, AuthorResponse author) {
        ArticleEmbeddingPayload payload = ArticleEmbeddingPayload.fromArticle(article, author, null);
        String description = loadDescription(payload, article.getId());
        syncVector(ArticleEmbeddingPayload.fromArticle(article, author, description), article.getId());
    }

    public void deleteArticleEmbedding(java.util.UUID articleId) {
        try {
            llmIntegrationService.deleteFromVectorStore(articleId);
        } catch (Exception ex) {
            log.warn("Vector store delete failed for article {}. Continue with article removal.", articleId);
        }
    }

    private String loadDescription(ArticleEmbeddingPayload payload, java.util.UUID articleId) {
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

    private void syncVector(ArticleEmbeddingPayload payload, java.util.UUID articleId) {
        try {
            llmIntegrationService.addToVectorStore(payload);
        } catch (Exception ex) {
            log.warn("Vector store sync failed for article {}. Continue without embedding.", articleId);
        }
    }
}
