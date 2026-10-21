package com.llm.api;

import com.llm.dto.article.ArticleDescriptionResponse;
import com.llm.dto.article.ArticleEmbeddingRequest;
import com.llm.services.facade.ArticleDescriptionFacadeService;
import org.springframework.web.bind.annotation.*;

/**
 * Defines article description controller.
 */
@RestController
@RequestMapping("/api/v1/assistant")
public class ArticleDescriptionController {

    private final ArticleDescriptionFacadeService articleDescriptionFacadeService;

/**
 * Performs article description controller.
 * @param articleDescriptionFacadeService the articleDescriptionFacadeService
 */
    public ArticleDescriptionController(ArticleDescriptionFacadeService articleDescriptionFacadeService) {
        this.articleDescriptionFacadeService = articleDescriptionFacadeService;
    }

    @PostMapping("/description")
/**
 * Returns generate description.
 * @return the result of the operation
 */
    public ArticleDescriptionResponse generateDescription(@RequestBody ArticleEmbeddingRequest payload) {
        return articleDescriptionFacadeService.generateDescription(payload);
    }
}

