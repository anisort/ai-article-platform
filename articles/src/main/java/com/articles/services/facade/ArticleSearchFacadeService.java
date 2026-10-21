package com.articles.services.facade;

import com.articles.config.CacheNames;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.user.UserBriefResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.LlmIntegrationService;
import com.articles.services.integration.UserIntegrationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Defines article search facade service.
 */
@Service
public class ArticleSearchFacadeService {

    private final ArticleService articleService;
    private final UserIntegrationService userIntegrationService;
    private final LlmIntegrationService llmIntegrationService;

    public ArticleSearchFacadeService(
            ArticleService articleService,
            UserIntegrationService userIntegrationService,
            LlmIntegrationService llmIntegrationService
    ) {
        this.articleService = articleService;
        this.userIntegrationService = userIntegrationService;
        this.llmIntegrationService = llmIntegrationService;
    }

    @Cacheable(
            cacheNames = CacheNames.SEARCH_BY_SEMANTIC,
            key = "#query == null ? '' : #query.trim().toLowerCase()"
    )
/**
 * Returns search by semantic.
 * @param query the query
 * @return the result of the operation
 */
    public List<ArticlePreviewResponse> searchBySemantic(String query) {
        if (query == null || query.isBlank()) {
            return mapToPreview(articleService.getPublishedArticles());
        }

        List<UUID> articleIds = llmIntegrationService.searchArticleIds(query);
        if (articleIds.isEmpty()) {
            return List.of();
        }

        List<Article> articles = articleService.getPublishedArticlesByIds(articleIds);
        Map<UUID, Article> byId = articles.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(articles);

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
/**
 * Returns search by title or author.
 * @param query the query
 * @return the result of the operation
 */
    public List<ArticlePreviewResponse> searchByTitleOrAuthor(String query) {
        if (query == null || query.isBlank()) {
            return mapToPreview(articleService.getPublishedArticles());
        }

        List<Article> articles = articleService.searchPublishedArticlesByTitle(query.trim());
        return mapToPreview(articles);
    }

/**
 * Returns map to preview.
 * @param articles the articles
 * @return the result of the operation
 */
    private List<ArticlePreviewResponse> mapToPreview(List<Article> articles) {
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }
}
