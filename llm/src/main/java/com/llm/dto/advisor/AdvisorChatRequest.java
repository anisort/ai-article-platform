package com.llm.dto.advisor;

import java.util.UUID;

public record AdvisorChatRequest(
        String message,
        UUID conversationId
) {
}
