package com.articles.repositories;

import com.articles.entities.ArticleLike;
import com.articles.entities.ArticleLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, ArticleLikeId> {
    List<ArticleLike> findByUserId(UUID userId);
    Optional<ArticleLike> findByUserIdAndArticle_Id(UUID userId, UUID articleId);
}
