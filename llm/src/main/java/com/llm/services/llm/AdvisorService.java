package com.llm.services.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdvisorService {

    private final VertexAiGeminiChatModel chatModel;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    public AdvisorService(VertexAiGeminiChatModel chatModel, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
    }

    public ChatResponse getAdvice(String prompt, UUID conversationId) {
        String systemMessage = "You are a concise article advisor. Use retrieved context when relevant and say when context is insufficient.";
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
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
