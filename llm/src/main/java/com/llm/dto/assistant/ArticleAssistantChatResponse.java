package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantChat;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleAssistantChatResponse(
        UUID id,
        String title,
        ZonedDateTime createdAt
) {
    public static ArticleAssistantChatResponse fromEntity(ArticleAssistantChat chat) {
        return new ArticleAssistantChatResponse(chat.getId(), chat.getTitle(), chat.getCreatedAt());
    }
}
