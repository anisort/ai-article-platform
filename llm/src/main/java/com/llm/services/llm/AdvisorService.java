package com.llm.services.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Defines advisor service.
 */
@Service
public class AdvisorService {

    private final VertexAiGeminiChatModel chatModel;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    @Value("${llm.prompts.advisor-system}")
    private String systemMessage;

/**
 * Performs advisor service.
 * @param chatModel the chatModel
 * @param vectorStore the vectorStore
 * @param chatMemory the chatMemory
 */
    public AdvisorService(VertexAiGeminiChatModel chatModel, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
    }

/**
 * Returns get advice.
 * @param prompt the prompt
 * @param conversationId the conversationId
 * @return the result of the operation
 */
    public ChatResponse getAdvice(String prompt, UUID conversationId) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest.builder()
                                                .filterExpression("published == true")
                                                .topK(6)
                                                .build()
                                )
                                .build()
                )
                .build()
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .system(systemMessage)
                .user(prompt)
                .call()
                .chatResponse();
    }
}
