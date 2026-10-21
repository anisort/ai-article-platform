package com.llm.services.facade;

import com.llm.dto.article.ArticleDescriptionResponse;
import com.llm.dto.article.ArticleEmbeddingRequest;
import com.llm.services.llm.AssistantService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Defines article description facade service.
 */
@Service
public class ArticleDescriptionFacadeService {

    private final AssistantService assistantService;

/**
 * Performs article description facade service.
 * @param assistantService the assistantService
 */
    public ArticleDescriptionFacadeService(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

/**
 * Returns generate description.
 * @param payload the payload
 * @return the result of the operation
 */
    public ArticleDescriptionResponse generateDescription(ArticleEmbeddingRequest payload) {
        String prompt = """
                Create a concise article description in 2-3 sentences based on the provided content.
                Title: %s
                Author: %s
                Content:
                %s
                """.formatted(payload.title(), payload.author(), payload.content());

        ChatResponse response = assistantService.getResponse(prompt, UUID.randomUUID());
        String description = response == null ? null : response.getResult().getOutput().getText();
        return ArticleDescriptionResponse.of(description);
    }
}

