package com.llm.dto;

import com.llm.entities.AssistantMessageRole;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleAssistantMessageResponse(
        UUID id,
        AssistantMessageRole role,
        String content,
        ZonedDateTime createdAt
) {
}
