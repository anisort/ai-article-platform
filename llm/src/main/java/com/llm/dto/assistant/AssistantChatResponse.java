package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantChat;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AssistantChatResponse(
        UUID id,
        String title,
        ZonedDateTime createdAt
) {
    public static AssistantChatResponse fromEntity(ArticleAssistantChat chat) {
        return new AssistantChatResponse(chat.getId(), chat.getTitle(), chat.getCreatedAt());
    }
}

