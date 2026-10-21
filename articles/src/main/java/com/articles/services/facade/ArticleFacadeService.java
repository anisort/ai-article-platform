package com.articles.services.facade;

import com.articles.config.CacheNames;
import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.article.EditArticlePageResponse;
import com.articles.dto.assistant.ArticleAssistantChatSummary;
import com.articles.dto.assistant.ArticleAssistantMessageSummary;
import com.articles.dto.user.UserBriefResponse;
import com.articles.dto.article.CreateArticleRequest;
import com.articles.dto.article.ArticlePreviewResponse;
import com.articles.dto.image.ArticleImageResponse;
import com.articles.entities.Article;
import com.articles.services.core.ArticleEmbeddingService;
import com.articles.services.core.ArticleImageService;
import com.articles.services.core.ArticleLikeService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.LlmIntegrationService;
import com.articles.services.integration.UserIntegrationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Defines article facade service.
 */
@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final ArticleImageService articleImageService;
    private final UserIntegrationService userIntegrationService;
    private final ArticleEmbeddingService articleEmbeddingService;
    private final LlmIntegrationService llmIntegrationService;

    public ArticleFacadeService(
            ArticleService articleService,
            ArticleLikeService articleLikeService,
            ArticleImageService articleImageService,
            UserIntegrationService userIntegrationService,
            ArticleEmbeddingService articleEmbeddingService,
            LlmIntegrationService llmIntegrationService
    ) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.articleImageService = articleImageService;
        this.userIntegrationService = userIntegrationService;
        this.articleEmbeddingService = articleEmbeddingService;
        this.llmIntegrationService = llmIntegrationService;
    }

    @Cacheable(cacheNames = CacheNames.ARTICLES_LIST, key = "'all'")
/**
 * Returns get published articles.
 * @return the result of the operation
 */
    public List<ArticlePreviewResponse> getPublishedArticles() {
        List<Article> articles = articleService.getPublishedArticles();
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticlePreviewResponse.
                        fromEntity(article, authors.get(article.getAuthorId())))
                .toList();
    }

    @Cacheable(
            cacheNames = CacheNames.ARTICLE_BY_ID,
            key = "#articleId.toString().concat(':').concat(#requesterId == null ? 'anon' : #requesterId.toString())"
    )
/**
 * Returns get article.
 * @param articleId the articleId
 * @param requesterId the requesterId
 * @return the result of the operation
 */
    public ArticleWithUserResponse getArticle(UUID articleId, UUID requesterId) {
        Article article = requesterId == null
                ? articleService.requirePublished(articleId)
                : articleService.requireAccessible(articleId, requesterId);

        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(List.of(article));
        long likesCount = articleLikeService.getLikesCount(article.getId());
        boolean likedByRequester = requesterId != null && articleLikeService.isArticleLikedByUser(article.getId(), requesterId);
        return ArticleWithUserResponse.mapToResponse(
                article,
                authors.get(article.getAuthorId()),
                likesCount,
                likedByRequester
        );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.ARTICLES_LIST, key = "'all'"),
            @CacheEvict(cacheNames = CacheNames.ARTICLE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_SEMANTIC, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.RECOMMENDATIONS_ANON, allEntries = true)
    })
/**
 * Returns create.
 * @param request the request
 * @param authorId the authorId
 * @return the result of the operation
 */
    public ArticleWithUserResponse create(CreateArticleRequest request, UUID authorId) {
        Article article = articleService.create(request, authorId);
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(List.of(article));
        UserBriefResponse author = authors.get(article.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(article, author);
        return ArticleWithUserResponse.mapToResponse(article, author, 0, false);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.ARTICLES_LIST, key = "'all'"),
            @CacheEvict(cacheNames = CacheNames.ARTICLE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_SEMANTIC, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.RECOMMENDATIONS_ANON, allEntries = true)
    })
/**
 * Returns update.
 * @param articleId the articleId
 * @param request the request
 * @param requesterId the requesterId
 * @return the result of the operation
 */
    public ArticleWithUserResponse update(UUID articleId, CreateArticleRequest request, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        Article updatedArticle = articleService.update(article, request);
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(List.of(updatedArticle));
        UserBriefResponse author = authors.get(updatedArticle.getAuthorId());
        articleEmbeddingService.syncArticleEmbedding(updatedArticle, author);
        return ArticleWithUserResponse.mapToResponse(updatedArticle, author, 0, false);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.ARTICLES_LIST, key = "'all'"),
            @CacheEvict(cacheNames = CacheNames.ARTICLE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.SEARCH_BY_SEMANTIC, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.RECOMMENDATIONS_ANON, allEntries = true)
    })
/**
 * Performs delete.
 * @param articleId the articleId
 * @param requesterId the requesterId
 */
    public void delete(UUID articleId, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        articleEmbeddingService.deleteArticleEmbedding(articleId);
        articleLikeService.deleteByArticleId(articleId);
        articleImageService.deleteByArticleId(articleId);
        articleService.delete(article);
    }

/**
 * Returns get edit page.
 * @param articleId the articleId
 * @param requesterId the requesterId
 * @return the result of the operation
 */
    public EditArticlePageResponse getEditPage(UUID articleId, UUID requesterId) {
        Article article = articleService.requireOwned(articleId, requesterId);
        Map<UUID, UserBriefResponse> authors = userIntegrationService.getAuthors(List.of(article));
        long likesCount = articleLikeService.getLikesCount(article.getId());
        boolean likedByRequester = articleLikeService.isArticleLikedByUser(article.getId(), requesterId);
        ArticleWithUserResponse articleResponse = ArticleWithUserResponse.mapToResponse(
                article,
                authors.get(article.getAuthorId()),
                likesCount,
                likedByRequester
        );

        List<ArticleImageResponse> images = articleImageService.listByArticleId(articleId).stream()
                .map(ArticleImageResponse::fromEntity)
                .toList();

        List<ArticleAssistantChatSummary> chats = llmIntegrationService.listArticleChats(articleId);
        UUID selectedChatId = chats.isEmpty() ? null : chats.getFirst().id();
        List<ArticleAssistantMessageSummary> messages = selectedChatId == null
                ? List.of()
                : llmIntegrationService.listArticleChatMessages(articleId, selectedChatId);

        return new EditArticlePageResponse(
                articleResponse,
                images,
                chats,
                selectedChatId,
                messages
        );
    }

}
