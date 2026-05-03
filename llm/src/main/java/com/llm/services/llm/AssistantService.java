package com.llm.services.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class AssistantService {

    private final VertexAiGeminiChatModel chatModel;
    private final ChatMemory chatMemory;

    public AssistantService(VertexAiGeminiChatModel chatModel, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

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

    public ChatResponse getMultimodalResponse(String prompt, UUID chatId, MultipartFile file) {
        String systemMessage = "You are a helpful assistant.";
        String contentType = file.getContentType();
        MimeType mimeType = (contentType != null)
                ? MimeTypeUtils.parseMimeType(contentType)
                : MimeTypeUtils.APPLICATION_OCTET_STREAM;

        Resource resource = file.getResource();

        ChatResponse ragPrompt = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build()
                .prompt()
                .system(systemMessage)
                .user(u -> u.text(prompt)
                        .media(mimeType, resource))
                .call()
                .chatResponse();
        
        return getResponse(ragPrompt != null ? ragPrompt.getResult().getOutput().getText() : null, chatId);
    }
}
