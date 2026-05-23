package com.llm.services.facade;

import com.llm.dto.advisor.AdvisorChatRequest;
import com.llm.dto.advisor.AdvisorChatResponse;
import com.llm.services.llm.AdvisorService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdvisorChatFacadeService {

    private final AdvisorService advisorService;

    public AdvisorChatFacadeService(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    public AdvisorChatResponse advisorChat(AdvisorChatRequest request) {
        UUID conversationId = request.conversationId() == null ? UUID.randomUUID() : request.conversationId();
        ChatResponse response = advisorService.getAdvice(request.message(), conversationId);
        String answer = response == null ? null : response.getResult().getOutput().getText();
        return AdvisorChatResponse.of(answer, conversationId);
    }
}
