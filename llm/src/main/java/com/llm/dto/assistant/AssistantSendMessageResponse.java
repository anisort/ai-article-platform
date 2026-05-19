package com.llm.dto.assistant;

import java.util.UUID;

public record AssistantSendMessageResponse(
        UUID chatId,
        AssistantMessageResponse assistantMessage
) {
}

