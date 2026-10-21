package com.llm.services.llm;

import com.llm.dto.article.ArticleEmbeddingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Defines article vector service.
 */
@Service
public class ArticleVectorService {

    private static final Logger log = LoggerFactory.getLogger(ArticleVectorService.class);

    private final VectorStore vectorStore;

/**
 * Performs article vector service.
 * @param vectorStore the vectorStore
 */
    public ArticleVectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

/**
 * Performs add or update.
 * @param article the article
 */
    public void addOrUpdate(ArticleEmbeddingRequest article) {
        if (article == null || article.id() == null) {
            return;
        }

        String id = article.id().toString();
        vectorStore.delete(List.of(id));

        String content = String.format(
                "Title: %s\nAuthor: %s\nDescription: %s\nImage: %s",
                article.title(),
                article.author(),
                article.content(),
                article.image()
        );
        Document document = new Document(
                id,
                content,
                Map.of(
                        "article_id", id,
                        "published", article.published()
                )
        );
        vectorStore.add(List.of(document));
    }

/**
 * Performs delete.
 * @param articleId the articleId
 */
    public void delete(UUID articleId) {
        if (articleId == null) {
            return;
        }
        vectorStore.delete(List.of(articleId.toString()));
    }

/**
 * Returns similarity search article ids.
 * @param query the query
 * @param similarityThreshold the similarityThreshold
 * @param topK the topK
 * @return the result of the operation
 */
    public List<UUID> similaritySearchArticleIds(String query, double similarityThreshold, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .similarityThreshold(similarityThreshold)
                .topK(topK)
                .filterExpression("published == true")
                .build();
        try {
            return vectorStore.similaritySearch(request).stream()
                    .map(document -> document.getMetadata().get("article_id"))
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(UUID::fromString)
                    .toList();
        } catch (Exception ex) {
            log.error(
                    "vectorStore.similaritySearch failed. query='{}', threshold={}, topK={}",
                    query,
                    similarityThreshold,
                    topK,
                    ex
            );
            throw ex;
        }
    }
}

