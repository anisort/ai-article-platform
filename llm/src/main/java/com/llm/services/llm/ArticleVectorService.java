package com.llm.services.llm;

import com.llm.dto.ArticleEmbeddingDocument;
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
        addOrUpdateBatch(List.of(article));
    }

    public void addOrUpdateBatch(List<ArticleEmbeddingDocument> articles) {
        if (articles == null || articles.isEmpty()) {
            return;
        }

        List<String> ids = articles.stream()
                .map(ArticleEmbeddingDocument::id)
                .filter(id -> id != null)
                .map(UUID::toString)
                .toList();

        if (!ids.isEmpty()) {
            vectorStore.delete(ids);
        }

        List<Document> documents = articles.stream()
                .filter(article -> article.id() != null)
                .map(article -> {
                    String content = String.format(
                            "Title: %s\nAuthor: %s\nDescription: %s\nImage: %s",
                            article.title(),
                            article.author(),
                            article.description(),
                            article.image()
                    );
                    return new Document(
                            article.id().toString(),
                            content,
                            Map.of("article_id", article.id().toString())
                    );
                })
                .toList();

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
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
