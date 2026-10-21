package com.articles.services.core;

import com.articles.entities.Article;
import com.articles.entities.ArticleLike;
import com.articles.repositories.ArticleLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Defines article like service.
 */
@Service
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;

/**
 * Performs article like service.
 * @param articleLikeRepository the articleLikeRepository
 */
    public ArticleLikeService(ArticleLikeRepository articleLikeRepository) {
        this.articleLikeRepository = articleLikeRepository;
    }

    @Transactional(readOnly = true)
/**
 * Returns get liked articles.
 * @param userId the userId
 * @return the result of the operation
 */
    public List<Article> getLikedArticles(UUID userId) {
        return articleLikeRepository.findByUserId(userId).stream()
                .map(ArticleLike::getArticle)
                .toList();
    }

    @Transactional(readOnly = true)
/**
 * Returns get recent liked articles.
 * @param userId the userId
 * @param limit the limit
 * @return the result of the operation
 */
    public List<Article> getRecentLikedArticles(UUID userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return articleLikeRepository.findByUserIdOrderByCreatedAtDesc(
                        userId,
                        org.springframework.data.domain.PageRequest.of(0, safeLimit)
                ).stream()
                .map(ArticleLike::getArticle)
                .toList();
    }

    @Transactional(readOnly = true)
/**
 * Returns get popular published article ids.
 * @param limit the limit
 * @return the result of the operation
 */
    public List<UUID> getPopularPublishedArticleIds(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return articleLikeRepository.findPopularPublishedArticleIds(
                org.springframework.data.domain.PageRequest.of(0, safeLimit)
        );
    }

    @Transactional
/**
 * Returns toggle like.
 * @param userId the userId
 * @param article the article
 * @return the result of the operation
 */
    public boolean toggleLike(UUID userId, Article article) {
        return articleLikeRepository.findByUserIdAndArticle_Id(userId, article.getId())
                .map(existingLike -> {
                    articleLikeRepository.delete(existingLike);
                    return false;
                })
                .orElseGet(() -> {
                    ArticleLike articleLike = new ArticleLike();
                    articleLike.setUserId(userId);
                    articleLike.setArticle(article);
                    articleLikeRepository.save(articleLike);
                    return true;
                });
    }

    @Transactional(readOnly = true)
/**
 * Returns get likes count.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public long getLikesCount(UUID articleId) {
        return articleLikeRepository.countByArticle_Id(articleId);
    }

    @Transactional(readOnly = true)
/**
 * Returns is article liked by user.
 * @param articleId the articleId
 * @param userId the userId
 * @return the result of the operation
 */
    public boolean isArticleLikedByUser(UUID articleId, UUID userId) {
        return articleLikeRepository.existsByUserIdAndArticle_Id(userId, articleId);
    }

    @Transactional
/**
 * Performs delete by article id.
 * @param articleId the articleId
 */
    public void deleteByArticleId(UUID articleId) {
        articleLikeRepository.deleteByArticle_Id(articleId);
    }
}
