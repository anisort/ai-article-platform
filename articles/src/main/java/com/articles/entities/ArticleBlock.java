package com.articles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "article_block")
@Getter
@Setter
public class ArticleBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id")
    private Article article;

    private int position;

    @Enumerated(EnumType.STRING)
    private BlockType type;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "code_language", length = 50)
    private String codeLanguage;

    @Column(name = "code", columnDefinition = "text")
    private String code;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Column(name = "image_caption", columnDefinition = "text")
    private String imageCaption;

}
