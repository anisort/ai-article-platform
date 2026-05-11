package com.llm.api;

import com.llm.dto.AdvisorChatRequest;
import com.llm.dto.AdvisorChatResponse;
import com.llm.services.facade.AdvisorChatFacade;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/advisor")
public class AdvisorChatController {

    private final AdvisorChatFacade advisorChatFacade;

    public AdvisorChatController(AdvisorChatFacade advisorChatFacade) {
        this.advisorChatFacade = advisorChatFacade;
    }

    @PostMapping("/chat")
    public AdvisorChatResponse advisorChat(@RequestBody AdvisorChatRequest request) {
        return advisorChatFacade.advisorChat(request);
    }
}
