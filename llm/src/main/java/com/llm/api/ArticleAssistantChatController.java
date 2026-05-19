package com.llm.api;

import com.llm.dto.assistant.AssistantChatResponse;
import com.llm.dto.assistant.AssistantCreateChatRequest;
import com.llm.dto.assistant.AssistantMessageResponse;
import com.llm.dto.assistant.AssistantSendMessageRequest;
import com.llm.dto.assistant.AssistantSendMessageResponse;
import com.llm.services.chat.ArticleAssistantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant/articles/{articleId}/chats")
public class ArticleAssistantChatController {

    private final ArticleAssistantService articleAssistantService;

    public ArticleAssistantChatController(ArticleAssistantService articleAssistantService) {
        this.articleAssistantService = articleAssistantService;
    }

    @PostMapping
    public AssistantChatResponse createArticleChat(
            @PathVariable UUID articleId,
            @RequestBody(required = false) AssistantCreateChatRequest request
    ) {
        return articleAssistantService.createChat(articleId, request == null ? null : request.title());
    }

    @GetMapping
    public List<AssistantChatResponse> listArticleChats(@PathVariable UUID articleId) {
        return articleAssistantService.listChats(articleId);
    }

    @GetMapping("/{chatId}/messages")
    public List<AssistantMessageResponse> listArticleChatMessages(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId
    ) {
        return articleAssistantService.listMessages(articleId, chatId);
    }

    @PostMapping("/{chatId}/messages")
    public AssistantSendMessageResponse sendArticleChatMessage(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId,
            @RequestBody AssistantSendMessageRequest request
    ) {
        return articleAssistantService.sendMessage(articleId, chatId, request);
    }
}

