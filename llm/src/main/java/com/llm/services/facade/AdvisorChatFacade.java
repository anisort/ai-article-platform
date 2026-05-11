package com.llm.services.facade;

import com.llm.dto.AdvisorChatRequest;
import com.llm.dto.AdvisorChatResponse;
import com.llm.services.llm.AdvisorService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdvisorChatFacade {

    private final AdvisorService advisorService;

    public AdvisorChatFacade(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    public AdvisorChatResponse advisorChat(AdvisorChatRequest request) {
        UUID conversationId = request.conversationId() == null ? UUID.randomUUID() : request.conversationId();
        ChatResponse response = advisorService.getAdvice(request.message(), conversationId);
        String answer = response == null ? null : response.getResult().getOutput().getText();
        return new AdvisorChatResponse(answer, conversationId);
    }
}
