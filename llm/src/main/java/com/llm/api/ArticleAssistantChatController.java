package com.llm.api;

import com.llm.dto.ArticleAssistantChatResponse;
import com.llm.dto.ArticleAssistantMessageResponse;
import com.llm.dto.CreateArticleAssistantChatRequest;
import com.llm.dto.SendArticleAssistantMessageRequest;
import com.llm.dto.SendArticleAssistantMessageResponse;
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
    public ArticleAssistantChatResponse createArticleChat(
            @PathVariable UUID articleId,
            @RequestBody(required = false) CreateArticleAssistantChatRequest request
    ) {
        return articleAssistantService.createChat(articleId, request == null ? null : request.title());
    }

    @GetMapping
    public List<ArticleAssistantChatResponse> listArticleChats(@PathVariable UUID articleId) {
        return articleAssistantService.listChats(articleId);
    }

    @GetMapping("/{chatId}/messages")
    public List<ArticleAssistantMessageResponse> listArticleChatMessages(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId
    ) {
        return articleAssistantService.listMessages(articleId, chatId);
    }

    @PostMapping("/{chatId}/messages")
    public SendArticleAssistantMessageResponse sendArticleChatMessage(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId,
            @RequestBody SendArticleAssistantMessageRequest request
    ) {
        return articleAssistantService.sendMessage(articleId, chatId, request);
    }
}
