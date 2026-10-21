package com.llm.services.llm;

import com.llm.dto.assistant.AssistantResponseMode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Defines assistant service.
 */
@Service
public class AssistantService {

    private final VertexAiGeminiChatModel chatModel;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;

/**
 * Performs assistant service.
 * @param chatModel the chatModel
 * @param chatMemory the chatMemory
 * @param vectorStore the vectorStore
 */
    public AssistantService(VertexAiGeminiChatModel chatModel, ChatMemory chatMemory, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
    }

/**
 * Returns get response.
 * @param prompt the prompt
 * @param chatId the chatId
 * @return the result of the operation
 */
    public ChatResponse getResponse(String prompt, UUID chatId) {
        String systemMessage = "You are a helpful assistant.";
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build()
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .system(systemMessage)
                .user(prompt)
                .call()
                .chatResponse();
    }

/**
 * Returns get multimodal response.
 * @param prompt the prompt
 * @param chatId the chatId
 * @param file the file
 * @return the result of the operation
 */
    public ChatResponse getMultimodalResponse(String prompt, UUID chatId, MultipartFile file) {
        String systemMessage = "You are a helpful assistant.";
        String contentType = file.getContentType();
        MimeType mimeType = (contentType != null)
                ? MimeTypeUtils.parseMimeType(contentType)
                : MimeTypeUtils.APPLICATION_OCTET_STREAM;

        Resource resource = file.getResource();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build()
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .system(systemMessage)
                .user(u -> u.text(prompt)
                        .media(mimeType, resource))
                .call()
                .chatResponse();
    }

/**
 * Returns get article assistant response.
 * @param prompt the prompt
 * @param chatId the chatId
 * @param mode the mode
 * @return the result of the operation
 */
    public ChatResponse getArticleAssistantResponse(String prompt, UUID chatId, AssistantResponseMode mode) {
        return getArticleAssistantResponse(prompt, chatId, mode, false);
    }

/**
 * Returns get article assistant response.
 * @param prompt the prompt
 * @param chatId the chatId
 * @param mode the mode
 * @param useKnowledgeBase the useKnowledgeBase
 * @return the result of the operation
 */
    public ChatResponse getArticleAssistantResponse(String prompt, UUID chatId, AssistantResponseMode mode, boolean useKnowledgeBase) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        if (useKnowledgeBase) {
            builder.defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    QuestionAnswerAdvisor.builder(vectorStore).build()
            );
        } else {
            builder.defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build()
            );
        }

        return builder.build()
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .system(articleAssistantSystemPrompt(mode))
                .user(prompt)
                .call()
                .chatResponse();
    }

/**
 * Returns article assistant system prompt.
 * @param mode the mode
 * @return the result of the operation
 */
    private String articleAssistantSystemPrompt(AssistantResponseMode mode) {
        AssistantResponseMode safeMode = mode == null ? AssistantResponseMode.THINKING : mode;
        return switch (safeMode) {
            case CODE -> "You are an article coding assistant. Respond with practical code-first answers. Prefer fenced code blocks with short notes.";
            case PRECISE -> "You are an article writing assistant. Respond with concise, exact, and polished phrasing.";
            case THINKING -> "You are an article assistant. Think through options, compare tradeoffs briefly, and provide a clear recommendation.";
        };
    }
}
