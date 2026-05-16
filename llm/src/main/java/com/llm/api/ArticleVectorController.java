package com.llm.api;

import com.llm.dto.article.ArticleEmbeddingDocument;
import com.llm.services.llm.ArticleVectorService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vector/articles")
public class ArticleVectorController {

    private final ArticleVectorService articleVectorService;

    public ArticleVectorController(ArticleVectorService articleVectorService) {
        this.articleVectorService = articleVectorService;
    }

    @PostMapping
    public void addArticleToVectorStore(@RequestBody ArticleEmbeddingDocument document) {
        articleVectorService.addOrUpdate(document);
    }

    @DeleteMapping("/{articleId}")
    public void deleteArticleFromVectorStore(@PathVariable UUID articleId) {
        articleVectorService.delete(articleId);
    }
}
