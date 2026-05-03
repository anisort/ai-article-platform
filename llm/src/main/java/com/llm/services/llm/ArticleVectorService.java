package com.llm.services.llm;

import com.llm.dto.ArticleEmbeddingDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArticleVectorService {

    private final VectorStore vectorStore;

    public ArticleVectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addOrUpdate(ArticleEmbeddingDocument article) {
        vectorStore.delete(List.of(article.id().toString()));

        String content = String.format(
                "Title: %s\nAuthor: %s\nDescription: %s\nImage: %s",
                article.title(),
                article.author(),
                article.description(),
                article.image()
        );
        Document document = new Document(
                article.id().toString(),
                content,
                Map.of("article_id", article.id().toString())
        );
        vectorStore.add(List.of(document));
    }
}
