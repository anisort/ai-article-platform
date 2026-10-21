package com.llm.dto.advisor;

import java.util.UUID;

/**
 * Carries advisor chat response.
 */
public record AdvisorChatResponse(
        String answer,
        UUID conversationId
) {
/**
 * Returns of.
 * @param answer the answer
 * @param conversationId the conversationId
 * @return the result of the operation
 */
    public static AdvisorChatResponse of(String answer, UUID conversationId) {
        return new AdvisorChatResponse(answer, conversationId);
    }
}
