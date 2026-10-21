package com.llm.services.chat;

import com.llm.dto.assistant.AssistantChatResponse;
import com.llm.dto.assistant.AssistantMessageResponse;
import com.llm.dto.assistant.AssistantResponseMode;
import com.llm.dto.assistant.AssistantSendMessageRequest;
import com.llm.dto.assistant.AssistantSendMessageResponse;
import com.llm.entities.ArticleAssistantChat;
import com.llm.entities.ArticleAssistantMessage;
import com.llm.entities.AssistantMessageRole;
import com.llm.repositories.ArticleAssistantChatRepository;
import com.llm.repositories.ArticleAssistantMessageRepository;
import com.llm.services.llm.AssistantService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Application service that manages article assistant chats and their messages.
 *
 * <p>Persists chats and messages through {@link ArticleAssistantChatRepository} and
 * {@link ArticleAssistantMessageRepository}, and delegates response generation to
 * {@link AssistantService}.</p>
 */
/**
 * Defines article assistant service.
 */
@Service
public class ArticleAssistantService {

    private final ArticleAssistantChatRepository chatRepository;
    private final ArticleAssistantMessageRepository messageRepository;
    private final AssistantService assistantService;

    /**
     * Creates the service with the collaborators required for chat persistence and response generation.
     *
     * @param chatRepository    repository for assistant chat entities
     * @param messageRepository repository for assistant message entities
     * @param assistantService  service that produces assistant responses
     */
    public ArticleAssistantService(
            ArticleAssistantChatRepository chatRepository,
            ArticleAssistantMessageRepository messageRepository,
            AssistantService assistantService
    ) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.assistantService = assistantService;
    }

/**
 * Returns create chat.
 * @param articleId the articleId
 * @param title the title
 * @return the result of the operation
 */
    public AssistantChatResponse createChat(UUID articleId, String title) {
        ArticleAssistantChat chat = new ArticleAssistantChat();
        chat.setArticleId(articleId);
        chat.setTitle((title == null || title.isBlank()) ? "New chat" : title.trim());
        ArticleAssistantChat saved = chatRepository.save(chat);
        return AssistantChatResponse.fromEntity(saved);
    }

/**
 * Returns list chats.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public List<AssistantChatResponse> listChats(UUID articleId) {
        return chatRepository.findByArticleIdOrderByCreatedAtDesc(articleId).stream()
                .map(AssistantChatResponse::fromEntity)
                .toList();
    }

/**
 * Returns list messages.
 * @param articleId the articleId
 * @param chatId the chatId
 * @return the result of the operation
 */
    public List<AssistantMessageResponse> listMessages(UUID articleId, UUID chatId) {
        ArticleAssistantChat chat = getChat(articleId, chatId);
        return messageRepository.findByChatOrderByCreatedAtAsc(chat).stream()
                .map(AssistantMessageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AssistantSendMessageResponse sendMessage(
            UUID articleId,
            UUID chatId,
            AssistantSendMessageRequest request
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

        return new AssistantSendMessageResponse(chatId, AssistantMessageResponse.fromEntity(savedAssistant));
    }

/**
 * Returns get chat.
 * @param articleId the articleId
 * @param chatId the chatId
 * @return the result of the operation
 */
    private ArticleAssistantChat getChat(UUID articleId, UUID chatId) {
        ArticleAssistantChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        if (!chat.getArticleId().equals(articleId)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return chat;
    }

}

