package com.llm.dto.advisor;

import java.util.UUID;

public record AdvisorChatResponse(
        String answer,
        UUID conversationId
) {
    public static AdvisorChatResponse of(String answer, UUID conversationId) {
        return new AdvisorChatResponse(answer, conversationId);
    }
}
