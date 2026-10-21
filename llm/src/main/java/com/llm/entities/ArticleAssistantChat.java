package com.llm.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Defines article assistant chat.
 */
@Entity
@Table(name = "article_assistant_chat")
@Getter
@Setter
public class ArticleAssistantChat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID articleId;

    private String title;

    @CreationTimestamp
    private ZonedDateTime createdAt;

}
