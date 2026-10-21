package com.llm.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Defines article assistant message.
 */
@Entity
@Table(name = "article_assistant_message")
@Getter
@Setter
public class ArticleAssistantMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ArticleAssistantChat chat;

    @Enumerated(EnumType.STRING)
    private AssistantMessageRole role;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}
