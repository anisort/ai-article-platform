package com.llm.dto;

import java.util.UUID;

public record SendArticleAssistantMessageResponse(
        UUID chatId,
        ArticleAssistantMessageResponse assistantMessage
) {
}
