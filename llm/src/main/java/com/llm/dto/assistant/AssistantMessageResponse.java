package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantMessage;
import com.llm.entities.AssistantMessageRole;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AssistantMessageResponse(
        UUID id,
        AssistantMessageRole role,
        String content,
        ZonedDateTime createdAt
) {
    public static AssistantMessageResponse fromEntity(ArticleAssistantMessage message) {
        return new AssistantMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

