package com.articles.services;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.ArticleEmbeddingDocument;
import com.articles.dto.ArticleEmbeddingDraft;
import com.articles.dto.ArticleImageResponse;
import com.articles.dto.AuthorResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.dto.LikedArticlesResponse;
import com.articles.dto.PublishedArticlePreviewResponse;
import com.articles.dto.SeedArticleRequest;
import com.articles.dto.ToggleArticleLikeResponse;
import com.articles.entities.Article;
import com.articles.entities.ArticleImage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleFacadeService {
    private static final Logger log = LoggerFactory.getLogger(ArticleFacadeService.class);

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final ArticleImageService articleImageService;
    private final UserService userService;
    private final LlmIntegrationService llmIntegrationService;
    private final FileStorageService fileStorageService;
    private final String seedToken;
    private final boolean seedSkipLlmDescription;
    private final boolean seedSkipVectorSync;
    private final int seedVectorBatchSize;
    private final int seedVectorRetryCount;
    private final long seedVectorRetryDelayMs;
    private final long seedVectorThrottleMs;
    private final long seedVectorMaxRetryDelayMs;
    private final boolean seedVectorRetryUntilSuccess;

    public ArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            ArticleImageService articleImageService,
            UserService userService,
            LlmIntegrationService llmIntegrationService,
            FileStorageService fileStorageService,
            @Value("${app.seed.token:}") String seedToken,
            @Value("${app.seed.skip-llm-description:true}") boolean seedSkipLlmDescription,
            @Value("${app.seed.skip-vector-sync:true}") boolean seedSkipVectorSync,
            @Value("${app.seed.vector.batch-size:30}") int seedVectorBatchSize,
            @Value("${app.seed.vector.retry-count:4}") int seedVectorRetryCount,
            @Value("${app.seed.vector.retry-delay-ms:600}") long seedVectorRetryDelayMs,
            @Value("${app.seed.vector.throttle-ms:120}") long seedVectorThrottleMs,
            @Value("${app.seed.vector.max-retry-delay-ms:20000}") long seedVectorMaxRetryDelayMs,
            @Value("${app.seed.vector.retry-until-success:true}") boolean seedVectorRetryUntilSuccess
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.articleImageService = articleImageService;
        this.userService = userService;
        this.llmIntegrationService = llmIntegrationService;
        this.fileStorageService = fileStorageService;
        this.seedToken = seedToken;
        this.seedSkipLlmDescription = seedSkipLlmDescription;
        this.seedSkipVectorSync = seedSkipVectorSync;
        this.seedVectorBatchSize = seedVectorBatchSize;
        this.seedVectorRetryCount = seedVectorRetryCount;
        this.seedVectorRetryDelayMs = seedVectorRetryDelayMs;
        this.seedVectorThrottleMs = seedVectorThrottleMs;
        this.seedVectorMaxRetryDelayMs = seedVectorMaxRetryDelayMs;
        this.seedVectorRetryUntilSuccess = seedVectorRetryUntilSuccess;
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

    public List<PublishedArticlePreviewResponse> getRecommendations(UUID userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));

        List<Article> likedArticles = articleLikeService.getLikedArticles(userId);
        Set<UUID> excludedIds = likedArticles.stream()
                .map(Article::getId)
                .collect(Collectors.toSet());

        LinkedHashSet<UUID> recommendedIds = new LinkedHashSet<>(
                llmIntegrationService.recommendArticleIds(
                        likedArticles.stream().map(Article::getId).toList(),
                        List.copyOf(excludedIds),
                        safeLimit
                )
        );

        List<Article> recommended = articleService.getPublishedArticlesByIds(List.copyOf(recommendedIds));
        Map<UUID, Article> recommendedById = recommended.stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));
        Map<UUID, AuthorResponse> recommendedAuthors = userService.getAuthors(recommended);

        List<PublishedArticlePreviewResponse> result = recommendedIds.stream()
                .map(recommendedById::get)
                .filter(Objects::nonNull)
                .map(article -> PublishedArticlePreviewResponse.fromEntity(article, recommendedAuthors.get(article.getAuthorId())))
                .limit(safeLimit)
                .toList();

        if (result.size() >= safeLimit) {
            return result;
        }

        // Cold start / sparse semantic matches fallback.
        Set<UUID> alreadyIncluded = result.stream().map(PublishedArticlePreviewResponse::id).collect(Collectors.toSet());
        List<PublishedArticlePreviewResponse> fallback = getPublishedArticles().stream()
                .filter(article -> !excludedIds.contains(article.id()))
                .filter(article -> !alreadyIncluded.contains(article.id()))
                .limit(safeLimit - result.size())
                .toList();

        return java.util.stream.Stream.concat(result.stream(), fallback.stream())
                .limit(safeLimit)
                .toList();
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
        syncArticleEmbedding(article, author, false);
        return ArticleWithUserResponse.mapToResponse(article, author);
    }

    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = getOwnedArticle(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(updatedArticle));
        AuthorResponse author = authors.get(updatedArticle.getAuthorId());
        syncArticleEmbedding(updatedArticle, author, false);
        return ArticleWithUserResponse.mapToResponse(updatedArticle, author);
    }

    public ToggleArticleLikeResponse toggleLike(UUID articleId, UUID userId) {
        Article article = getPublishedArticle(articleId);
        boolean liked = articleLikeService.toggleLike(userId, article);
        return new ToggleArticleLikeResponse(articleId, liked);
    }

    public List<ArticleWithUserResponse> seedArticles(List<SeedArticleRequest> requests, String providedSeedToken) {
        if (seedToken == null || seedToken.isBlank() || !Objects.equals(seedToken, providedSeedToken)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid seed token");
        }
        if (articleService.count() > 0) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Articles table is not empty");
        }

        List<Article> saved = requests.stream()
                .map(request -> articleService.create(request.toCreateRequest(), request.authorId()))
                .toList();

        Map<UUID, AuthorResponse> authors = userService.getAuthors(saved);

        if (!seedSkipVectorSync) {
            int safeBatch = Math.max(1, Math.min(seedVectorBatchSize, 200));
            for (int i = 0; i < saved.size(); i += safeBatch) {
                int end = Math.min(i + safeBatch, saved.size());
                List<Article> batch = saved.subList(i, end);
                List<ArticleEmbeddingDocument> documents = batch.stream()
                        .map(article -> toEmbeddingDocument(article, authors.get(article.getAuthorId()), true))
                        .toList();
                syncVectorBatchWithRetry(documents, true);
            }
        }

        return saved.stream()
                .map(article -> ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public int reindexEmbeddings(String providedSeedToken, int batchSize, long delayMs) {
        if (seedToken == null || seedToken.isBlank() || !Objects.equals(seedToken, providedSeedToken)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid seed token");
        }

        int requestedBatch = batchSize <= 0 ? seedVectorBatchSize : batchSize;
        int safeBatch = Math.max(1, Math.min(requestedBatch, 200));
        long safeDelayMs = Math.max(0L, delayMs);
        List<Article> articles = articleService.getPublishedArticles();
        if (articles.isEmpty()) {
            return 0;
        }

        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        int processed = 0;
        for (int i = 0; i < articles.size(); i += safeBatch) {
            int end = Math.min(i + safeBatch, articles.size());
            List<Article> batch = articles.subList(i, end);

            List<ArticleEmbeddingDocument> documents = batch.stream()
                    .map(article -> toEmbeddingDocument(article, authors.get(article.getAuthorId()), true))
                    .toList();

            if (!documents.isEmpty()) {
                syncVectorBatchWithRetry(documents, true);
                processed += documents.size();
            }
            if (safeDelayMs > 0 && end < articles.size()) {
                sleepQuietly(safeDelayMs);
            }
        }
        return processed;
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

    private void syncArticleEmbedding(Article article, AuthorResponse author, boolean seedMode) {
        ArticleEmbeddingDocument document = toEmbeddingDocument(article, author, seedMode);
        if (seedMode && seedSkipVectorSync) {
            return;
        }
        syncVectorWithRetry(document, article.getId(), seedMode);
    }

    private ArticleEmbeddingDocument toEmbeddingDocument(Article article, AuthorResponse author, boolean seedMode) {
        String authorName = author == null ? "Unknown" : author.name();
        String description = localDescriptionFallback(article);

        if (!(seedMode && seedSkipLlmDescription)) {
            ArticleEmbeddingDraft draft = new ArticleEmbeddingDraft(
                    article.getId(),
                    buildArticleText(article),
                    article.getTitle(),
                    authorName,
                    article.getImageUrl()
            );
            try {
                description = llmIntegrationService.generateDescription(draft);
            } catch (Exception ex) {
                log.warn("LLM description generation failed for article {}. Fallback to local summary text.", article.getId());
                description = localDescriptionFallback(article);
            }
        }

        if (!StringUtils.hasText(description)) {
            description = localDescriptionFallback(article);
        }

        return new ArticleEmbeddingDocument(
                article.getId(),
                description,
                article.getTitle(),
                authorName,
                article.getImageUrl()
        );
    }

    private String buildArticleText(Article article) {
        return article.getMarkdownContent() == null ? "" : article.getMarkdownContent();
    }

    private String localDescriptionFallback(Article article) {
        String text = buildArticleText(article).trim();
        if (!StringUtils.hasText(text)) {
            return article.getTitle() == null ? "Programming article" : article.getTitle();
        }
        int maxLen = Math.min(text.length(), 500);
        return text.substring(0, maxLen);
    }

    private void syncVectorWithRetry(ArticleEmbeddingDocument document, UUID articleId, boolean seedMode) {
        int attempts = seedMode ? Math.max(1, seedVectorRetryCount) : 1;
        long baseDelay = Math.max(0L, seedVectorRetryDelayMs);
        long maxDelay = Math.max(baseDelay, seedVectorMaxRetryDelayMs);
        int i = 1;
        while (true) {
            try {
                llmIntegrationService.addToVectorStore(document);
                return;
            } catch (Exception ex) {
                boolean retryForever = seedMode && seedVectorRetryUntilSuccess && isQuotaOrRateLimitError(ex);
                if (!retryForever && i >= attempts) {
                    log.warn("Vector store sync failed for article {} after {} attempt(s). Seed continues without embedding.", articleId, attempts);
                    return;
                }
                long waitMs = computeBackoffDelay(baseDelay, maxDelay, i);
                log.warn("Vector sync retry for article {} (attempt {}), waiting {} ms", articleId, i, waitMs);
                sleepQuietly(waitMs);
                i++;
            }
        }
    }

    private void syncVectorBatchWithRetry(List<ArticleEmbeddingDocument> documents, boolean seedMode) {
        int attempts = seedMode ? Math.max(1, seedVectorRetryCount) : 1;
        long baseDelay = Math.max(0L, seedVectorRetryDelayMs);
        long maxDelay = Math.max(baseDelay, seedVectorMaxRetryDelayMs);
        int i = 1;
        while (true) {
            try {
                llmIntegrationService.addToVectorStoreBatch(documents);
                if (seedMode && seedVectorThrottleMs > 0) {
                    sleepQuietly(seedVectorThrottleMs);
                }
                return;
            } catch (Exception ex) {
                boolean retryForever = seedMode && seedVectorRetryUntilSuccess && isQuotaOrRateLimitError(ex);
                if (!retryForever && i >= attempts) {
                    log.warn("Vector store batch sync failed after {} attempt(s). Batch size: {}", attempts, documents.size());
                    return;
                }
                long waitMs = computeBackoffDelay(baseDelay, maxDelay, i);
                log.warn("Vector batch sync retry (attempt {}, batch size {}), waiting {} ms", i, documents.size(), waitMs);
                sleepQuietly(waitMs);
                i++;
            }
        }
    }

    private boolean isQuotaOrRateLimitError(Exception ex) {
        String message = ex.getMessage();
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("resource_exhausted")
                || normalized.contains("quota exceeded")
                || normalized.contains("429")
                || normalized.contains("too many requests");
    }

    private long computeBackoffDelay(long baseDelay, long maxDelay, int attempt) {
        if (baseDelay <= 0) {
            return 0L;
        }
        int exponent = Math.max(0, Math.min(attempt - 1, 10));
        long multiplier = 1L << exponent;
        long raw = baseDelay * multiplier;
        long capped = Math.min(raw, Math.max(baseDelay, maxDelay));
        long jitter = (long) (Math.random() * Math.max(200L, baseDelay / 2));
        return capped + jitter;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }

}
