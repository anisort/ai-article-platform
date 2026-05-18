package com.articles.dto.assistant;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ArticleAssistantMessageSummary(
        UUID id,
        String role,
        String content,
        ZonedDateTime createdAt
) {
}
