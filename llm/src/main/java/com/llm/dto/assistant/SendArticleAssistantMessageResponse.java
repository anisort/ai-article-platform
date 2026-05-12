package com.llm.dto.assistant;

import java.util.UUID;

public record SendArticleAssistantMessageResponse(
        UUID chatId,
        ArticleAssistantMessageResponse assistantMessage
) {
    public static SendArticleAssistantMessageResponse of(UUID chatId, ArticleAssistantMessageResponse assistantMessage) {
        return new SendArticleAssistantMessageResponse(chatId, assistantMessage);
    }
}
