package com.llm.services.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
public class AdvisorService {

    private final VertexAiGeminiChatModel chatModel;
    private final VectorStore vectorStore;

    public AdvisorService(VertexAiGeminiChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    public ChatResponse getAdvice(String prompt) {
        String systemMessage = "You are an expert in {topic}. Answer the following question: {question}";
        return ChatClient.builder(chatModel)
                .build()
                .prompt()
                .advisors(
                        QuestionAnswerAdvisor
                                .builder(vectorStore)
                                .build()
                )
                .system(systemMessage)
                .user(prompt)
                .call()
                .chatResponse();
    }
}
