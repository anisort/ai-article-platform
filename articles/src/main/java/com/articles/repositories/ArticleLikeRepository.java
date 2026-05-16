package com.articles.repositories;

import com.articles.entities.ArticleLike;
import com.articles.entities.ArticleLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, ArticleLikeId> {
    List<ArticleLike> findByUserId(UUID userId);
    List<ArticleLike> findByUserIdOrderByCreatedAtDesc(UUID userId, org.springframework.data.domain.Pageable pageable);
    Optional<ArticleLike> findByUserIdAndArticle_Id(UUID userId, UUID articleId);
    long countByArticle_Id(UUID articleId);
    boolean existsByUserIdAndArticle_Id(UUID userId, UUID articleId);

    @Query("""
            SELECT al.article.id
            FROM ArticleLike al
            WHERE al.article.published = true
            GROUP BY al.article.id
            ORDER BY COUNT(al.article.id) DESC
            """)
    List<UUID> findPopularPublishedArticleIds(org.springframework.data.domain.Pageable pageable);
}
