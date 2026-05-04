package com.llm.dto;

import java.util.UUID;

public record AdvisorChatResponse(
        String answer,
        UUID conversationId
) {
}
