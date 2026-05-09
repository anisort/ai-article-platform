package com.articles.services.facade;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.user.AuthorResponse;
import com.articles.dto.article.CreateArticleRequest;
import com.articles.dto.article.PublishedArticlePreviewResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleEmbeddingService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final UserService userService;
    private final ArticleEmbeddingService articleEmbeddingService;

    public ArticleFacadeService(
            ArticleService articleService,
            UserService userService,
            ArticleEmbeddingService articleEmbeddingService
    ) {
        this.articleService = articleService;
        this.userService = userService;
        this.articleEmbeddingService = articleEmbeddingService;
    }

    public List<PublishedArticlePreviewResponse> getPublishedArticles() {
        List<Article> articles = articleService.getPublishedArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public List<PublishedArticlePreviewResponse> getPublishedArticlesByAuthor(UUID authorId) {
        List<Article> articles = articleService.getPublishedArticlesByAuthor(authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public ArticleWithUserResponse getArticle(UUID articleId, UUID requesterId) {
        Article article = requesterId == null
                ? articleService.requirePublished(articleId)
                : articleService.requireAccessible(articleId, requesterId);

        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        return ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId()));
    }

    public ArticleWithUserResponse create(CreateArticleRequest request, UUID authorId) {
        Article article = articleService.create(request, authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        AuthorResponse author = authors.get(article.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(article, author);
        return ArticleWithUserResponse.mapToResponse(article, author);
    }

    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(updatedArticle));
        AuthorResponse author = authors.get(updatedArticle.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(updatedArticle, author);
        return ArticleWithUserResponse.mapToResponse(updatedArticle, author);
    }

}





