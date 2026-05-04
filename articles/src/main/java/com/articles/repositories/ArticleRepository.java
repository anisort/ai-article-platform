package com.articles.repositories;

import com.articles.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    List<Article> findByPublishedTrueOrderByCreatedAtDesc();
    List<Article> findByPublishedTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    List<Article> findByIdInAndPublishedTrue(List<UUID> ids);
    List<Article> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
    List<Article> findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(UUID authorId);

    Optional<Article> findByIdAndPublishedTrue(UUID id);
}
