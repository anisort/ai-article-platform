package com.articles.services.facade;

import com.articles.config.CacheNames;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.LlmIntegrationService;
import com.articles.services.integration.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleRecommendationFacadeService {
    private static final int PERSONALIZED_LIKES_WINDOW = 20;

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final LlmIntegrationService llmIntegrationService;
    private final UserService userService;

    public ArticleRecommendationFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            LlmIntegrationService llmIntegrationService,
            UserService userService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.llmIntegrationService = llmIntegrationService;
        this.userService = userService;
    }

    @Cacheable(
            cacheNames = CacheNames.RECOMMENDATIONS_ANON,
            key = "#limit",
            condition = "#userId == null"
    )
    public List<ArticlePreviewResponse> getRecommendations(UUID userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        List<Article> latest = articleService.getPublishedArticles();
        if (latest.isEmpty()) {
            return List.of();
        }

        List<UUID> orderedIds = new ArrayList<>();
        Set<UUID> excludedIds = new LinkedHashSet<>();
        List<UUID> personalized = List.of();

        if (userId != null) {
            excludedIds.addAll(
                    articleService.getArticlesByAuthor(userId).stream()
                            .map(Article::getId)
                            .toList()
            );

            List<Article> likedArticles = articleLikeService.getRecentLikedArticles(userId, PERSONALIZED_LIKES_WINDOW);
            excludedIds.addAll(likedArticles.stream().map(Article::getId).toList());

            if (!likedArticles.isEmpty()) {
                personalized = llmIntegrationService.recommendArticleIds(
                        likedArticles.stream().map(Article::getId).toList(),
                        List.copyOf(excludedIds),
                        safeLimit
                );
            }
        }

        List<UUID> fallback = buildPopularAndLatestMix(latest, safeLimit * 2, excludedIds, Set.copyOf(orderedIds));
        orderedIds = mergePersonalizedWithFallback(personalized, fallback, safeLimit, excludedIds);

        List<Article> selected = articleService.getPublishedArticlesByIds(new ArrayList<>(orderedIds));
        Map<UUID, Article> byId = selected.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        Map<UUID, AuthorResponse> authors = userService.getAuthors(selected);

        return orderedIds.stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .limit(safeLimit)
                .toList();
    }

    private List<UUID> mergePersonalizedWithFallback(
            List<UUID> personalized,
            List<UUID> fallback,
            int limit,
            Set<UUID> excludedIds
    ) {
        if (personalized == null || personalized.isEmpty()) {
            return fallback.stream().limit(limit).toList();
        }

        List<UUID> result = new ArrayList<>();
        Set<UUID> seen = new LinkedHashSet<>();
        int p = 0;
        int f = 0;

        while (result.size() < limit && (p < personalized.size() || f < fallback.size())) {
            int personalizedBatch = 0;
            while (result.size() < limit && personalizedBatch < 3 && p < personalized.size()) {
                UUID id = personalized.get(p++);
                if (!excludedIds.contains(id) && seen.add(id)) {
                    result.add(id);
                    personalizedBatch++;
                }
            }

            if (result.size() >= limit) {
                break;
            }

            while (f < fallback.size()) {
                UUID id = fallback.get(f++);
                if (!excludedIds.contains(id) && seen.add(id)) {
                    result.add(id);
                    break;
                }
            }
        }

        return result;
    }

    private List<UUID> buildPopularAndLatestMix(
            List<Article> latestArticles,
            int limit,
            Set<UUID> excludedIds,
            Set<UUID> alreadyIncluded
    ) {
        List<UUID> popularIds = articleLikeService.getPopularPublishedArticleIds(limit * 2);
        List<UUID> latestIds = latestArticles.stream().map(Article::getId).toList();
        List<UUID> mixed = new ArrayList<>();

        int i = 0;
        int j = 0;
        while (mixed.size() < limit && (i < popularIds.size() || j < latestIds.size())) {
            if (i < popularIds.size()) {
                UUID id = popularIds.get(i++);
                if (!excludedIds.contains(id) && !alreadyIncluded.contains(id) && !mixed.contains(id)) {
                    mixed.add(id);
                }
            }
            if (mixed.size() >= limit) {
                break;
            }
            if (j < latestIds.size()) {
                UUID id = latestIds.get(j++);
                if (!excludedIds.contains(id) && !alreadyIncluded.contains(id) && !mixed.contains(id)) {
                    mixed.add(id);
                }
            }
        }
        return mixed;
    }
}
