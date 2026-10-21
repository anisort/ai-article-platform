package com.articles.api;

import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.services.facade.ArticleSearchFacadeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Defines article search controller.
 */
@RestController
@RequestMapping("/api/v1/search/articles")
public class ArticleSearchController {

    private final ArticleSearchFacadeService articleSearchFacadeService;

/**
 * Performs article search controller.
 * @param articleSearchFacadeService the articleSearchFacadeService
 */
    public ArticleSearchController(ArticleSearchFacadeService articleSearchFacadeService) {
        this.articleSearchFacadeService = articleSearchFacadeService;
    }

    @GetMapping
    public List<ArticlePreviewResponse> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String mode
    ) {
        if ("semantic".equalsIgnoreCase(mode)) {
            return articleSearchFacadeService.searchBySemantic(query);
        }
        return articleSearchFacadeService.searchByTitleOrAuthor(query);
    }
}
