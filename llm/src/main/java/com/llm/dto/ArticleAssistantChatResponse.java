package com.llm.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleAssistantChatResponse(
        UUID id,
        String title,
        ZonedDateTime createdAt
) {
}
