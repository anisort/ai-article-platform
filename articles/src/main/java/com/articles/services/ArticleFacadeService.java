package com.articles.services;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
import com.articles.dto.AuthorResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.dto.LikedArticlesResponse;
import com.articles.dto.PublishedArticlePreviewResponse;
import com.articles.dto.ToggleArticleLikeResponse;
import com.articles.entities.Article;
import com.articles.entities.ArticleBlock;
import com.articles.entities.BlockType;
import org.springframework.stereotype.Service;
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
    private final UserService userService;
    private final LlmIntegrationService llmIntegrationService;

    public ArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            UserService userService,
            LlmIntegrationService llmIntegrationService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.userService = userService;
        this.llmIntegrationService = llmIntegrationService;
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
        return article.getBlocks().stream()
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                .map(this::toTextPart)
                .filter(Objects::nonNull)
                .filter(part -> !part.isBlank())
                .collect(Collectors.joining("\n\n"));
    }

    private String toTextPart(ArticleBlock block) {
        if (block.getType() == BlockType.TEXT) {
            return block.getContent();
        }
        if (block.getType() == BlockType.CODE) {
            String code = block.getCode();
            if (code == null || code.isBlank()) {
                return null;
            }
            String lang = block.getCodeLanguage() == null ? "" : block.getCodeLanguage();
            return "```" + lang + "\n" + code + "\n```";
        }
        return null;
    }
}
