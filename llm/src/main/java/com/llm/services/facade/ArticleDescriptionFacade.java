package com.llm.services.facade;

import com.llm.dto.article.ArticleDescriptionResponse;
import com.llm.dto.article.ArticleEmbeddingDraft;
import com.llm.services.llm.AssistantService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArticleDescriptionFacade {

    private final AssistantService assistantService;

    public ArticleDescriptionFacade(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    public ArticleDescriptionResponse generateDescription(ArticleEmbeddingDraft draft) {
        String prompt = """
                Create a concise article description in 2-3 sentences based on the provided content.
                Title: %s
                Author: %s
                Content:
                %s
                """.formatted(draft.title(), draft.author(), draft.text());

        ChatResponse response = assistantService.getResponse(prompt, UUID.randomUUID());
        String description = response == null ? null : response.getResult().getOutput().getText();
        return ArticleDescriptionResponse.of(description);
    }
}
