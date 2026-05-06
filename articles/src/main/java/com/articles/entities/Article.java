package com.articles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "article")
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID authorId;

    private String title;

    private String imageUrl;

    private boolean published;

    private String summary;

    @Column(columnDefinition = "text")
    private String markdownContent;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}
