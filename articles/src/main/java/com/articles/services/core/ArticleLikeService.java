package com.articles.services.core;

import com.articles.entities.Article;
import com.articles.entities.ArticleLike;
import com.articles.repositories.ArticleLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;

    public ArticleLikeService(ArticleLikeRepository articleLikeRepository) {
        this.articleLikeRepository = articleLikeRepository;
    }

    @Transactional(readOnly = true)
    public List<Article> getLikedArticles(UUID userId) {
        return articleLikeRepository.findByUserId(userId).stream()
                .map(ArticleLike::getArticle)
                .toList();
    }

    @Transactional(readOnly = true)
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
    public List<UUID> getPopularPublishedArticleIds(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return articleLikeRepository.findPopularPublishedArticleIds(
                org.springframework.data.domain.PageRequest.of(0, safeLimit)
        );
    }

    @Transactional
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
    public long getLikesCount(UUID articleId) {
        return articleLikeRepository.countByArticle_Id(articleId);
    }

    @Transactional(readOnly = true)
    public boolean isArticleLikedByUser(UUID articleId, UUID userId) {
        return articleLikeRepository.existsByUserIdAndArticle_Id(userId, articleId);
    }

    @Transactional
    public void deleteByArticleId(UUID articleId) {
        articleLikeRepository.deleteByArticle_Id(articleId);
    }
}
