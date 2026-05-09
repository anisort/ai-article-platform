package com.articles.api;

import com.articles.dto.article.PublishedArticlePreviewResponse;
import com.articles.services.facade.ArticleSearchFacadeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search/articles")
public class ArticleSearchApi {

    private final ArticleSearchFacadeService articleSearchFacadeService;

    public ArticleSearchApi(ArticleSearchFacadeService articleSearchFacadeService) {
        this.articleSearchFacadeService = articleSearchFacadeService;
    }

    @GetMapping
    public List<PublishedArticlePreviewResponse> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String mode
    ) {
        if ("semantic".equalsIgnoreCase(mode)) {
            return articleSearchFacadeService.searchBySemantic(query);
        }
        return articleSearchFacadeService.searchByTitleOrAuthor(query);
    }
}
