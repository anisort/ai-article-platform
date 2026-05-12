package com.llm.services.llm;

import com.llm.dto.article.ArticleEmbeddingDocument;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArticleVectorService {

    private final VectorStore vectorStore;

    public ArticleVectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addOrUpdate(ArticleEmbeddingDocument article) {
        if (article == null || article.id() == null) {
            return;
        }

        String id = article.id().toString();
        vectorStore.delete(List.of(id));

        String content = String.format(
                "Title: %s\nAuthor: %s\nDescription: %s\nImage: %s",
                article.title(),
                article.author(),
                article.description(),
                article.image()
        );
        Document document = new Document(
                id,
                content,
                Map.of("article_id", id)
        );
        vectorStore.add(List.of(document));
    }

    public List<UUID> similaritySearchArticleIds(String query, double similarityThreshold, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .similarityThreshold(similarityThreshold)
                .topK(topK)
                .build();

        return vectorStore.similaritySearch(request).stream()
                .map(document -> document.getMetadata().get("article_id"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(UUID::fromString)
                .toList();
    }
}
