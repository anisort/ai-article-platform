package com.llm.api;

import com.llm.dto.ArticleDescriptionResponse;
import com.llm.dto.ArticleEmbeddingDocument;
import com.llm.dto.ArticleEmbeddingDraft;
import com.llm.services.llm.ArticleVectorService;
import com.llm.services.llm.AssistantService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AssistantApi {

    private final AssistantService assistantService;
    private final ArticleVectorService articleVectorService;

    public AssistantApi(AssistantService assistantService, ArticleVectorService articleVectorService) {
        this.assistantService = assistantService;
        this.articleVectorService = articleVectorService;
    }

    @PostMapping("/assistant/description")
    public ArticleDescriptionResponse generateDescription(@RequestBody ArticleEmbeddingDraft draft) {
        String prompt = """
                Create a concise article description in 2-3 sentences based on the provided content.
                Title: %s
                Author: %s
                Content:
                %s
                """.formatted(draft.title(), draft.author(), draft.text());

        ChatResponse response = assistantService.getResponse(prompt, UUID.randomUUID());
        String description = response == null ? null : response.getResult().getOutput().getText();
        return new ArticleDescriptionResponse(description);
    }

    @PostMapping("/vector/articles")
    public void addArticleToVectorStore(@RequestBody ArticleEmbeddingDocument document) {
        articleVectorService.addOrUpdate(document);
    }
}
