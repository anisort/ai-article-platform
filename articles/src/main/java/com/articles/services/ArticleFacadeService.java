package com.articles.services;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
import com.articles.dto.ArticleImageResponse;
import com.articles.dto.AuthorResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.dto.LikedArticlesResponse;
import com.articles.dto.PublishedArticlePreviewResponse;
import com.articles.dto.ToggleArticleLikeResponse;
import com.articles.entities.Article;
import com.articles.entities.ArticleImage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final ArticleImageService articleImageService;
    private final UserService userService;
    private final LlmIntegrationService llmIntegrationService;
    private final FileStorageService fileStorageService;

    public ArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            ArticleImageService articleImageService,
            UserService userService,
            LlmIntegrationService llmIntegrationService,
            FileStorageService fileStorageService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.articleImageService = articleImageService;
        this.userService = userService;
        this.llmIntegrationService = llmIntegrationService;
        this.fileStorageService = fileStorageService;
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

    public List<PublishedArticlePreviewResponse> searchPublishedArticles(String query, String mode) {
        if (query == null || query.isBlank()) {
            return getPublishedArticles();
        }

        if ("semantic".equalsIgnoreCase(mode)) {
            List<UUID> articleIds = llmIntegrationService.searchArticleIds(query);
            if (articleIds.isEmpty()) {
                return List.of();
            }

            List<Article> articles = articleService.getPublishedArticlesByIds(articleIds);
            Map<UUID, Article> byId = articles.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
            Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);

            return articleIds.stream()
                    .map(byId::get)
                    .filter(Objects::nonNull)
                    .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                    .toList();
        }

        String normalizedQuery = query.trim().toLowerCase();
        List<Article> articles = articleService.getPublishedArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .filter(article -> matchesTitleOrAuthor(article, authors.get(article.getAuthorId()), normalizedQuery))
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    private boolean matchesTitleOrAuthor(Article article, AuthorResponse author, String query) {
        if (article.getTitle() != null && article.getTitle().toLowerCase().contains(query)) {
            return true;
        }

        if (author == null) {
            return false;
        }

        return (author.name() != null && author.name().toLowerCase().contains(query))
                || (author.username() != null && author.username().toLowerCase().contains(query));
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
        AuthorResponse author = authors.get(article.getAuthorId());
        syncArticleEmbedding(article, author);
        return ArticleWithUserResponse.mapToResponse(article, author);
    }

    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = getOwnedArticle(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(updatedArticle));
        AuthorResponse author = authors.get(updatedArticle.getAuthorId());
        syncArticleEmbedding(updatedArticle, author);
        return ArticleWithUserResponse.mapToResponse(updatedArticle, author);
    }

    public ToggleArticleLikeResponse toggleLike(UUID articleId, UUID userId) {
        Article article = getPublishedArticle(articleId);
        boolean liked = articleLikeService.toggleLike(userId, article);
        return new ToggleArticleLikeResponse(articleId, liked);
    }

    public List<ArticleImageResponse> listArticleImages(UUID articleId, UUID requesterId) {
        getOwnedArticle(articleId, requesterId);
        return articleImageService.listByArticleId(articleId).stream()
                .map(ArticleImageResponse::fromEntity)
                .toList();
    }

    public ArticleImageResponse addArticleImage(UUID articleId, UUID requesterId, String imageUrl) {
        getOwnedArticle(articleId, requesterId);
        if (!StringUtils.hasText(imageUrl)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "imageUrl is required");
        }
        ArticleImage image = articleImageService.add(articleId, imageUrl);
        return ArticleImageResponse.fromEntity(image);
    }

    public void deleteArticleImage(UUID articleId, UUID imageId, UUID requesterId, String bearerToken) {
        getOwnedArticle(articleId, requesterId);
        ArticleImage image = articleImageService.findByIdAndArticleId(imageId, articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        fileStorageService.deleteByUrl(image.getImageUrl(), bearerToken);
        articleImageService.delete(image);
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

    private void syncArticleEmbedding(Article article, AuthorResponse author) {
        String authorName = author == null ? "Unknown" : author.name();
        ArticleEmbeddingDraft draft = new ArticleEmbeddingDraft(
                article.getId(),
                buildArticleText(article),
                article.getTitle(),
                authorName,
                article.getImageUrl()
        );

        String description = llmIntegrationService.generateDescription(draft);
        ArticleEmbeddingDocument document = new ArticleEmbeddingDocument(
                article.getId(),
                description,
                article.getTitle(),
                authorName,
                article.getImageUrl()
        );
        llmIntegrationService.addToVectorStore(document);
    }

    private String buildArticleText(Article article) {
        return article.getMarkdownContent() == null ? "" : article.getMarkdownContent();
    }
}
