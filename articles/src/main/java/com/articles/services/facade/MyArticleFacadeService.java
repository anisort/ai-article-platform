package com.articles.services.facade;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.dto.like.LikedArticlesResponse;
import com.articles.dto.like.ToggleArticleLikeResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MyArticleFacadeService {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final UserService userService;

    public MyArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            UserService userService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.userService = userService;
    }

    public List<ArticleWithUserResponse> getMyArticles(UUID authorId) {
        List<Article> articles = articleService.getArticlesByAuthor(authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticleWithUserResponse.mapToResponse(
                        article,
                        authors.get(article.getAuthorId()),
                        articleLikeService.getLikesCount(article.getId()),
                        false
                ))
                .toList();
    }

    public LikedArticlesResponse getLikedArticles(UUID userId) {
        return new LikedArticlesResponse(articleLikeService.getLikedArticleIds(userId));
    }

    public ToggleArticleLikeResponse toggleLike(UUID articleId, UUID userId) {
        Article article = articleService.requirePublished(articleId);
        boolean liked = articleLikeService.toggleLike(userId, article);
        long likesCount = articleLikeService.getLikesCount(articleId);
        return new ToggleArticleLikeResponse(articleId, liked, likesCount);
    }
}





