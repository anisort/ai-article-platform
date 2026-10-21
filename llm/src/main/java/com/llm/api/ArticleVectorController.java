package com.llm.api;

import com.llm.dto.article.ArticleEmbeddingRequest;
import com.llm.services.llm.ArticleVectorService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Defines article vector controller.
 */
@RestController
@RequestMapping("/api/v1/vector/articles")
public class ArticleVectorController {

    private final ArticleVectorService articleVectorService;

/**
 * Performs article vector controller.
 * @param articleVectorService the articleVectorService
 */
    public ArticleVectorController(ArticleVectorService articleVectorService) {
        this.articleVectorService = articleVectorService;
    }

    @PostMapping
/**
 * Performs add article to vector store.
 */
    public void addArticleToVectorStore(@RequestBody ArticleEmbeddingRequest payload) {
        articleVectorService.addOrUpdate(payload);
    }

    @DeleteMapping("/{articleId}")
/**
 * Performs delete article from vector store.
 */
    public void deleteArticleFromVectorStore(@PathVariable UUID articleId) {
        articleVectorService.delete(articleId);
    }
}

