package com.articles.services.facade;

import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.profile.ProfilePageResponse;
import com.articles.dto.profile.UserProfileResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProfilePageFacadeService {

    private final UserService userService;
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    public ProfilePageFacadeService(
            UserService userService,
            ArticleService articleService,
            ArticleLikeService articleLikeService
    ) {
        this.userService = userService;
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
    }

    public ProfilePageResponse getOwnProfilePage(UUID userId, String authorizationHeader) {
        UserProfileResponse profile = userService.getMyProfile(authorizationHeader);
        List<Article> ownArticles = articleService.getArticlesByAuthor(userId);
        List<Article> likedArticles = articleLikeService.getLikedArticles(userId);
        Map<UUID, AuthorResponse> authors = getAuthors(ownArticles, likedArticles);

        return new ProfilePageResponse(
                profile,
                mapArticlePreviews(ownArticles, authors),
                mapArticlePreviews(likedArticles, authors),
                List.of()
        );
    }

    public ProfilePageResponse getForeignProfilePage(UUID userId, String authorizationHeader) {
        UserProfileResponse profile = userService.getProfileById(userId, authorizationHeader);
        List<Article> publishedArticles = articleService.getPublishedArticlesByAuthor(userId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(publishedArticles);

        return new ProfilePageResponse(
                profile,
                List.of(),
                List.of(),
                publishedArticles.stream()
                        .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                        .toList()
        );
    }

    private Map<UUID, AuthorResponse> getAuthors(List<Article> ownArticles, List<Article> likedArticles) {
        List<Article> allArticles = new ArrayList<>(ownArticles.size() + likedArticles.size());
        allArticles.addAll(ownArticles);
        allArticles.addAll(likedArticles);
        return userService.getAuthors(allArticles);
    }

    private List<ArticlePreviewResponse> mapArticlePreviews(List<Article> articles, Map<UUID, AuthorResponse> authors) {
        return articles.stream()
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }
}
