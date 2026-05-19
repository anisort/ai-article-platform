package com.articles.services.facade;

import com.articles.config.CacheNames;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.LlmIntegrationService;
import com.articles.services.integration.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleSearchFacadeService {

    private final ArticleService articleService;
    private final UserService userService;
    private final LlmIntegrationService llmIntegrationService;
    private final ArticleFacadeService articleFacadeService;

    public ArticleSearchFacadeService(
            ArticleService articleService,
            UserService userService,
            LlmIntegrationService llmIntegrationService,
            ArticleFacadeService articleFacadeService
    ) {
        this.articleService = articleService;
        this.userService = userService;
        this.llmIntegrationService = llmIntegrationService;
        this.articleFacadeService = articleFacadeService;
    }

    @Cacheable(
            cacheNames = CacheNames.SEARCH_BY_SEMANTIC,
            key = "#query == null ? '' : #query.trim().toLowerCase()"
    )
    public List<ArticlePreviewResponse> searchBySemantic(String query) {
        if (query == null || query.isBlank()) {
            return articleFacadeService.getPublishedArticles();
        }

        List<UUID> articleIds = llmIntegrationService.searchArticleIds(query);
        if (articleIds.isEmpty()) {
            return List.of();
        }

        List<Article> articles = articleService.getPublishedArticlesByIds(articleIds);
        Map<UUID, Article> byId = articles.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);

        return articleIds.stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    @Cacheable(
            cacheNames = CacheNames.SEARCH_BY_TITLE,
            key = "#query == null ? '' : #query.trim().toLowerCase()"
    )
    public List<ArticlePreviewResponse> searchByTitleOrAuthor(String query) {
        if (query == null || query.isBlank()) {
            return articleFacadeService.getPublishedArticles();
        }

        List<Article> articles = articleService.searchPublishedArticlesByTitle(query.trim());
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }
}
