package com.articles.repositories;

import com.articles.entities.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, UUID> {
    List<ArticleLike> findByUserId(UUID userId);
    Optional<ArticleLike> findByUserIdAndArticle_Id(UUID userId, UUID articleId);
}
