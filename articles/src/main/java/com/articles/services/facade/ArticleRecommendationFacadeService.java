package com.articles.services.facade;

import com.articles.dto.article.PublishedArticlePreviewResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.LlmIntegrationService;
import com.articles.services.integration.UserService;
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

    public List<PublishedArticlePreviewResponse> getRecommendations(UUID userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        List<Article> latest = articleService.getPublishedArticles();
        if (latest.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<UUID> orderedIds = new LinkedHashSet<>();
        Set<UUID> excludedIds = new LinkedHashSet<>();

        if (userId != null) {
            List<Article> likedArticles = articleLikeService.getLikedArticles(userId);
            excludedIds.addAll(likedArticles.stream().map(Article::getId).toList());

            if (!likedArticles.isEmpty()) {
                List<UUID> personalized = llmIntegrationService.recommendArticleIds(
                        likedArticles.stream().map(Article::getId).toList(),
                        List.copyOf(excludedIds),
                        safeLimit
                );
                orderedIds.addAll(personalized);
            }
        }

        orderedIds.addAll(buildPopularAndLatestMix(latest, safeLimit, excludedIds, orderedIds));

        List<Article> selected = articleService.getPublishedArticlesByIds(new ArrayList<>(orderedIds));
        Map<UUID, Article> byId = selected.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        Map<UUID, AuthorResponse> authors = userService.getAuthors(selected);

        return orderedIds.stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .limit(safeLimit)
                .toList();
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

