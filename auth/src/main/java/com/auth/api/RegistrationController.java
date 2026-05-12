package com.auth.api;

import com.auth.dto.CreateUserRequest;
import com.auth.dto.UserResponse;
import com.auth.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public UserResponse save(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.fromEntity(registrationService.register(request));
    }
}
