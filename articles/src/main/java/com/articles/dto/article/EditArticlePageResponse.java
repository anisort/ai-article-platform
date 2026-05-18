package com.articles.dto.article;

import com.articles.dto.assistant.ArticleAssistantChatSummary;
import com.articles.dto.assistant.ArticleAssistantMessageSummary;
import com.articles.dto.image.ArticleImageResponse;

import java.util.List;
import java.util.UUID;

public record EditArticlePageResponse(
        ArticleWithUserResponse article,
        List<ArticleImageResponse> images,
        List<ArticleAssistantChatSummary> chats,
        UUID selectedChatId,
        List<ArticleAssistantMessageSummary> messages
) {
}
