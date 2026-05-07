package com.llm.api;

import com.llm.dto.ArticleDescriptionResponse;
import com.llm.dto.ArticleEmbeddingDocument;
import com.llm.dto.ArticleEmbeddingDraft;
import com.llm.dto.ArticleRecommendationRequest;
import com.llm.dto.ArticleRecommendationResponse;
import com.llm.dto.AdvisorChatRequest;
import com.llm.dto.AdvisorChatResponse;
import com.llm.dto.ArticleAssistantChatResponse;
import com.llm.dto.ArticleAssistantMessageResponse;
import com.llm.dto.CreateArticleAssistantChatRequest;
import com.llm.dto.SemanticSearchRequest;
import com.llm.dto.SemanticSearchResponse;
import com.llm.dto.SendArticleAssistantMessageRequest;
import com.llm.dto.SendArticleAssistantMessageResponse;
import com.llm.services.llm.AdvisorService;
import com.llm.services.llm.ArticleAssistantService;
import com.llm.services.llm.ArticleVectorService;
import com.llm.services.llm.ArticleRecommendationService;
import com.llm.services.llm.AssistantService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AssistantApi {

    private final AssistantService assistantService;
    private final ArticleVectorService articleVectorService;
    private final ArticleRecommendationService articleRecommendationService;
    private final AdvisorService advisorService;
    private final ArticleAssistantService articleAssistantService;

    public AssistantApi(
            AssistantService assistantService,
            ArticleVectorService articleVectorService,
            ArticleRecommendationService articleRecommendationService,
            AdvisorService advisorService,
            ArticleAssistantService articleAssistantService
    ) {
        this.assistantService = assistantService;
        this.articleVectorService = articleVectorService;
        this.articleRecommendationService = articleRecommendationService;
        this.advisorService = advisorService;
        this.articleAssistantService = articleAssistantService;
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

    @PostMapping("/search/articles")
    public SemanticSearchResponse searchArticles(@RequestBody SemanticSearchRequest request) {
        double threshold = request.similarityThreshold() == null ? 0.6 : request.similarityThreshold();
        int topK = request.topK() == null ? 20 : request.topK();
        return new SemanticSearchResponse(
                articleVectorService.similaritySearchArticleIds(request.query(), threshold, topK)
        );
    }

    @PostMapping("/recommend/articles")
    public ArticleRecommendationResponse recommendArticles(@RequestBody ArticleRecommendationRequest request) {
        int topK = request.topK() == null ? 20 : request.topK();
        return new ArticleRecommendationResponse(
                articleRecommendationService.recommendArticleIds(
                        request.likedArticleIds(),
                        request.excludeArticleIds(),
                        topK
                )
        );
    }

    @PostMapping("/advisor/chat")
    public AdvisorChatResponse advisorChat(@RequestBody AdvisorChatRequest request) {
        UUID conversationId = request.conversationId() == null ? UUID.randomUUID() : request.conversationId();
        ChatResponse response = advisorService.getAdvice(request.message(), conversationId);
        String answer = response == null ? null : response.getResult().getOutput().getText();
        return new AdvisorChatResponse(answer, conversationId);
    }

    @PostMapping("/advisor/articles/{articleId}/chats")
    public ArticleAssistantChatResponse createArticleChat(
            @PathVariable UUID articleId,
            @RequestBody(required = false) CreateArticleAssistantChatRequest request
    ) {
        return articleAssistantService.createChat(articleId, request == null ? null : request.title());
    }

    @GetMapping("/advisor/articles/{articleId}/chats")
    public List<ArticleAssistantChatResponse> listArticleChats(@PathVariable UUID articleId) {
        return articleAssistantService.listChats(articleId);
    }

    @GetMapping("/advisor/articles/{articleId}/chats/{chatId}/messages")
    public List<ArticleAssistantMessageResponse> listArticleChatMessages(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId
    ) {
        return articleAssistantService.listMessages(articleId, chatId);
    }

    @PostMapping("/advisor/articles/{articleId}/chats/{chatId}/messages")
    public SendArticleAssistantMessageResponse sendArticleChatMessage(
            @PathVariable UUID articleId,
            @PathVariable UUID chatId,
            @RequestBody SendArticleAssistantMessageRequest request
    ) {
        return articleAssistantService.sendMessage(articleId, chatId, request);
    }
}
