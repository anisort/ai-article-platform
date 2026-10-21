package com.llm.dto.advisor;

import java.util.UUID;

/**
 * Carries advisor chat request.
 */
public record AdvisorChatRequest(
        String message,
        UUID conversationId
) {
}
