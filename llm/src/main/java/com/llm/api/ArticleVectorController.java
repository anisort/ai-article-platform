package com.llm.api;

import com.llm.dto.ArticleEmbeddingDocument;
import com.llm.services.llm.ArticleVectorService;
import org.springframework.web.bind.annotation.*;

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
}
