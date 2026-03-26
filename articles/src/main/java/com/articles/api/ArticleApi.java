package com.articles.api;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.services.ArticleFacadeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleApi {

    private final ArticleFacadeService articleFacadeService;

    public ArticleApi(ArticleFacadeService articleFacadeService) {
        this.articleFacadeService = articleFacadeService;
    }

    @GetMapping
    public List<ArticleWithUserResponse> getArticles() {
        return articleFacadeService.getArticles();
    }

    @PostMapping
    public ArticleWithUserResponse create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateArticleRequest req) {
        UUID authorId = UUID.fromString(jwt.getClaimAsString("sub"));
        return articleFacadeService.create(req, authorId);
    }
}
