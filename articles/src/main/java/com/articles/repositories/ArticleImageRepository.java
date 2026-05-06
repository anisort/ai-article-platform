package com.articles.repositories;

import com.articles.entities.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, UUID> {
    List<ArticleImage> findByArticleIdOrderByIdDesc(UUID articleId);
    Optional<ArticleImage> findByIdAndArticleId(UUID id, UUID articleId);
}
