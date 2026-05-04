package com.llm.dto;

import java.util.UUID;

public record AdvisorChatRequest(
        String message,
        UUID conversationId
) {
}
