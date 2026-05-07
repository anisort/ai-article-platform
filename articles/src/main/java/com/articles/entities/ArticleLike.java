package com.articles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@IdClass(ArticleLikeId.class)
@Table(
        name = "article_like"
)
@Getter
@Setter
public class ArticleLike {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}
