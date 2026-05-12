package com.llm.api;

import com.llm.dto.article.ArticleDescriptionResponse;
import com.llm.dto.article.ArticleEmbeddingDraft;
import com.llm.services.facade.ArticleDescriptionFacade;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistant")
public class ArticleDescriptionController {

    private final ArticleDescriptionFacade articleDescriptionFacade;

    public ArticleDescriptionController(ArticleDescriptionFacade articleDescriptionFacade) {
        this.articleDescriptionFacade = articleDescriptionFacade;
    }

    @PostMapping("/description")
    public ArticleDescriptionResponse generateDescription(@RequestBody ArticleEmbeddingDraft draft) {
        return articleDescriptionFacade.generateDescription(draft);
    }
}
