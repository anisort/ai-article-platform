package com.llm.dto;

public record SendArticleAssistantMessageRequest(
        String message,
        AssistantResponseMode mode
) {
}
