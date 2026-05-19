package com.llm.dto.assistant;

public record AssistantSendMessageRequest(
        String message,
        AssistantResponseMode mode
) {
}
