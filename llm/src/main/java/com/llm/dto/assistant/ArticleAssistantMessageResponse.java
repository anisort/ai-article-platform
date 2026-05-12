package com.llm.dto.assistant;

import com.llm.entities.ArticleAssistantMessage;
import com.llm.entities.AssistantMessageRole;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleAssistantMessageResponse(
        UUID id,
        AssistantMessageRole role,
        String content,
        ZonedDateTime createdAt
) {
    public static ArticleAssistantMessageResponse fromEntity(ArticleAssistantMessage message) {
        return new ArticleAssistantMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
