package com.llm.dto.assistant;

public record SendArticleAssistantMessageRequest(
        String message,
        AssistantResponseMode mode
) {
}
