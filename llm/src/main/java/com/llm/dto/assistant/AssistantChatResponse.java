package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantChat;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Carries assistant chat response.
 */
public record AssistantChatResponse(
        UUID id,
        String title,
        ZonedDateTime createdAt
) {
/**
 * Returns from entity.
 * @param chat the chat
 * @return the result of the operation
 */
    public static AssistantChatResponse fromEntity(ArticleAssistantChat chat) {
        return new AssistantChatResponse(chat.getId(), chat.getTitle(), chat.getCreatedAt());
    }
}

