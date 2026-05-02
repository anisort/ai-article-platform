package com.articles.services;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.AuthorResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.dto.LikedArticlesResponse;
import com.articles.dto.PublishedArticlePreviewResponse;
import com.articles.dto.ToggleArticleLikeResponse;
import com.articles.entities.Article;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final UserService userService;

    public ArticleFacadeService(ArticleService articleService, ArticleLikeService articleLikeService, UserService userService) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.userService = userService;
    }

    public List<ArticleWithUserResponse> getArticles() {
        List<Article> articles = articleService.getArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public List<PublishedArticlePreviewResponse> getPublishedArticles() {
        List<Article> articles = articleService.getPublishedArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public List<ArticleWithUserResponse> getMyArticles(UUID authorId) {
        List<Article> articles = articleService.getArticlesByAuthor(authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public List<PublishedArticlePreviewResponse> getPublishedArticlesByAuthor(UUID authorId) {
        List<Article> articles = articleService.getPublishedArticlesByAuthor(authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public LikedArticlesResponse getLikedArticles(UUID userId) {
        return new LikedArticlesResponse(articleLikeService.getLikedArticleIds(userId));
    }

    public ArticleWithUserResponse getArticle(UUID articleId, UUID requesterId) {
        Article article = requesterId == null
                ? getPublishedArticle(articleId)
                : getAccessibleArticle(articleId, requesterId);

        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        return ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId()));
    }

    public ArticleWithUserResponse create(CreateArticleRequest request, UUID authorId) {
        Article article = articleService.create(request, authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        return ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId()));
    }

    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = getOwnedArticle(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(updatedArticle));
        return ArticleWithUserResponse.mapToResponse(updatedArticle, authors.get(updatedArticle.getAuthorId()));
    }

    public ToggleArticleLikeResponse toggleLike(UUID articleId, UUID userId) {
        Article article = getPublishedArticle(articleId);
        boolean liked = articleLikeService.toggleLike(userId, article);
        return new ToggleArticleLikeResponse(articleId, liked);
    }

    private Article getPublishedArticle(UUID articleId) {
        return articleService.getPublishedArticle(articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    private Article getAccessibleArticle(UUID articleId, UUID requesterId) {
        Article article = articleService.getById(articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        if (article.isPublished() || article.getAuthorId().equals(requesterId)) {
            return article;
        }

        throw new ResponseStatusException(NOT_FOUND);
    }

    private Article getOwnedArticle(UUID articleId, UUID requesterId) {
        Article article = articleService.getById(articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        if (!article.getAuthorId().equals(requesterId)) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        return article;
    }
}
