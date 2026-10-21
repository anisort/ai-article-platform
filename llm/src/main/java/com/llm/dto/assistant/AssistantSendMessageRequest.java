package com.llm.dto.assistant;

/**
 * Carries assistant send message request.
 */
public record AssistantSendMessageRequest(
        String message,
        AssistantResponseMode mode
) {
}
