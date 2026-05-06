package com.articles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "article_image")
@Getter
@Setter
public class ArticleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;
}
