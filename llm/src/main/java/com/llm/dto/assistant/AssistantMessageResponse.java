package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantMessage;
import com.llm.entities.AssistantMessageRole;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Carries assistant message response.
 */
public record AssistantMessageResponse(
        UUID id,
        AssistantMessageRole role,
        String content,
        ZonedDateTime createdAt
) {
/**
 * Returns from entity.
 * @param message the message
 * @return the result of the operation
 */
    public static AssistantMessageResponse fromEntity(ArticleAssistantMessage message) {
        return new AssistantMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

