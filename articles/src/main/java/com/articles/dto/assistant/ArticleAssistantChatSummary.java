package com.articles.dto.assistant;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Carries article assistant chat summary.
 */
public record ArticleAssistantChatSummary(
        UUID id,
        String title,
        ZonedDateTime createdAt
) {
}
