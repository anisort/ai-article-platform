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
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_assistant_message")
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

    public UUID getId() {
        return id;
    }

    public ArticleAssistantChat getChat() {
        return chat;
    }

    public void setChat(ArticleAssistantChat chat) {
        this.chat = chat;
    }

    public AssistantMessageRole getRole() {
        return role;
    }

    public void setRole(AssistantMessageRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
