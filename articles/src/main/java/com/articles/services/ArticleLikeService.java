package com.articles.services;

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
    public List<UUID> getLikedArticleIds(UUID userId) {
        return articleLikeRepository.findByUserId(userId).stream()
                .map(articleLike -> articleLike.getArticle().getId())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Article> getLikedArticles(UUID userId) {
        return articleLikeRepository.findByUserId(userId).stream()
                .map(ArticleLike::getArticle)
                .toList();
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
}
