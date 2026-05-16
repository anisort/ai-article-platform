package com.articles.services.facade;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.dto.article.CreateArticleRequest;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleEmbeddingService;
import com.articles.services.core.ArticleImageService;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final ArticleImageService articleImageService;
    private final UserService userService;
    private final ArticleEmbeddingService articleEmbeddingService;

    public ArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            ArticleImageService articleImageService,
            UserService userService,
            ArticleEmbeddingService articleEmbeddingService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.articleImageService = articleImageService;
        this.userService = userService;
        this.articleEmbeddingService = articleEmbeddingService;
    }

    public List<ArticlePreviewResponse> getPublishedArticles() {
        List<Article> articles = articleService.getPublishedArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public List<ArticlePreviewResponse> getPublishedArticlesByAuthor(UUID authorId) {
        List<Article> articles = articleService.getPublishedArticlesByAuthor(authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public ArticleWithUserResponse getArticle(UUID articleId, UUID requesterId) {
        Article article = requesterId == null
                ? articleService.requirePublished(articleId)
                : articleService.requireAccessible(articleId, requesterId);

        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        long likesCount = articleLikeService.getLikesCount(article.getId());
        boolean likedByRequester = requesterId != null && articleLikeService.isArticleLikedByUser(article.getId(), requesterId);
        return ArticleWithUserResponse.mapToResponse(
                article,
                authors.get(article.getAuthorId()),
                likesCount,
                likedByRequester
        );
    }

    public ArticleWithUserResponse create(CreateArticleRequest request, UUID authorId) {
        Article article = articleService.create(request, authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        AuthorResponse author = authors.get(article.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(article, author);
        return ArticleWithUserResponse.mapToResponse(article, author, 0, false);
    }

    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(updatedArticle));
        AuthorResponse author = authors.get(updatedArticle.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(updatedArticle, author);
        return ArticleWithUserResponse.mapToResponse(updatedArticle, author, 0, false);
    }

    public void delete(UUID articleId, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        articleEmbeddingService.deleteArticleEmbedding(articleId);
        articleLikeService.deleteByArticleId(articleId);
        articleImageService.deleteByArticleId(articleId);
        articleService.delete(article);
    }

}
