package com.llm.services.llm;

import com.llm.dto.ArticleAssistantChatResponse;
import com.llm.dto.ArticleAssistantMessageResponse;
import com.llm.dto.AssistantResponseMode;
import com.llm.dto.SendArticleAssistantMessageRequest;
import com.llm.dto.SendArticleAssistantMessageResponse;
import com.llm.entities.ArticleAssistantChat;
import com.llm.entities.ArticleAssistantMessage;
import com.llm.entities.AssistantMessageRole;
import com.llm.repositories.ArticleAssistantChatRepository;
import com.llm.repositories.ArticleAssistantMessageRepository;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleAssistantService {

    private final ArticleAssistantChatRepository chatRepository;
    private final ArticleAssistantMessageRepository messageRepository;
    private final AssistantService assistantService;

    public ArticleAssistantService(
            ArticleAssistantChatRepository chatRepository,
            ArticleAssistantMessageRepository messageRepository,
            AssistantService assistantService
    ) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.assistantService = assistantService;
    }

    public ArticleAssistantChatResponse createChat(UUID articleId, String title) {
        ArticleAssistantChat chat = new ArticleAssistantChat();
        chat.setArticleId(articleId);
        chat.setTitle((title == null || title.isBlank()) ? "New chat" : title.trim());
        ArticleAssistantChat saved = chatRepository.save(chat);
        return toChatResponse(saved);
    }

    public List<ArticleAssistantChatResponse> listChats(UUID articleId) {
        return chatRepository.findByArticleIdOrderByCreatedAtDesc(articleId).stream()
                .map(this::toChatResponse)
                .toList();
    }

    public List<ArticleAssistantMessageResponse> listMessages(UUID articleId, UUID chatId) {
        ArticleAssistantChat chat = getChat(articleId, chatId);
        return messageRepository.findByChatOrderByCreatedAtAsc(chat).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional
    public SendArticleAssistantMessageResponse sendMessage(
            UUID articleId,
            UUID chatId,
            SendArticleAssistantMessageRequest request
    ) {
        ArticleAssistantChat chat = getChat(articleId, chatId);
        String userMessage = request.message() == null ? "" : request.message().trim();

        ArticleAssistantMessage user = new ArticleAssistantMessage();
        user.setChat(chat);
        user.setRole(AssistantMessageRole.USER);
        user.setContent(userMessage);
        messageRepository.save(user);

        AssistantResponseMode mode = request.mode() == null ? AssistantResponseMode.THINKING : request.mode();
        ChatResponse response = assistantService.getArticleAssistantResponse(userMessage, chat.getId(), mode);

        String answer = response == null ? "" : response.getResult().getOutput().getText();
        ArticleAssistantMessage assistant = new ArticleAssistantMessage();
        assistant.setChat(chat);
        assistant.setRole(AssistantMessageRole.ASSISTANT);
        assistant.setContent(answer);
        ArticleAssistantMessage savedAssistant = messageRepository.save(assistant);

        return new SendArticleAssistantMessageResponse(chatId, toMessageResponse(savedAssistant));
    }

    private ArticleAssistantChat getChat(UUID articleId, UUID chatId) {
        ArticleAssistantChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        if (!chat.getArticleId().equals(articleId)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return chat;
    }

    private ArticleAssistantChatResponse toChatResponse(ArticleAssistantChat chat) {
        return new ArticleAssistantChatResponse(chat.getId(), chat.getTitle(), chat.getCreatedAt());
    }

    private ArticleAssistantMessageResponse toMessageResponse(ArticleAssistantMessage message) {
        return new ArticleAssistantMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
