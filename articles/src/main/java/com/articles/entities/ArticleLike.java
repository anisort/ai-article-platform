package com.articles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "article_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_article_like_user_article", columnNames = {"user_id", "article_id"})
        }
)
@Getter
@Setter
public class ArticleLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}
