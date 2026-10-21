package com.llm.api;

import com.llm.dto.advisor.AdvisorChatRequest;
import com.llm.dto.advisor.AdvisorChatResponse;
import com.llm.services.facade.AdvisorChatFacadeService;
import org.springframework.web.bind.annotation.*;

/**
 * Defines advisor chat controller.
 */
@RestController
@RequestMapping("/api/v1/advisor")
public class AdvisorChatController {

    private final AdvisorChatFacadeService advisorChatFacadeService;

/**
 * Performs advisor chat controller.
 * @param advisorChatFacadeService the advisorChatFacadeService
 */
    public AdvisorChatController(AdvisorChatFacadeService advisorChatFacadeService) {
        this.advisorChatFacadeService = advisorChatFacadeService;
    }

    @PostMapping("/chat")
/**
 * Returns advisor chat.
 * @return the result of the operation
 */
    public AdvisorChatResponse advisorChat(@RequestBody AdvisorChatRequest request) {
        return advisorChatFacadeService.advisorChat(request);
    }
}
