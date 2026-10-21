package com.llm.dto.assistant;

import java.util.UUID;

/**
 * Carries assistant send message response.
 */
public record AssistantSendMessageResponse(
        UUID chatId,
        AssistantMessageResponse assistantMessage
) {
}

