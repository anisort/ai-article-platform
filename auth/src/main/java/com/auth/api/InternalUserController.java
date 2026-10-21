package com.auth.api;

import com.auth.dto.UserBriefResponse;
import com.auth.services.InternalUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Defines internal user controller.
 */
@RestController
@RequestMapping("/api/v1/internal/users")
public class InternalUserController {

    private final InternalUserService internalUserService;

/**
 * Performs internal user controller.
 * @param internalUserService the internalUserService
 */
    public InternalUserController(InternalUserService internalUserService) {
        this.internalUserService = internalUserService;
    }

    @PostMapping("/batch")
/**
 * Returns batch.
 * @return the result of the operation
 */
    public List<UserBriefResponse> batch(@RequestBody List<UUID> ids) {
        return internalUserService.getByIds(ids).stream().map(UserBriefResponse::fromEntity).toList();
    }
}
