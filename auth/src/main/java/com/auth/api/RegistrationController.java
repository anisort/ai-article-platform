package com.auth.api;

import com.auth.dto.AvailabilityResponse;
import com.auth.dto.CreateUserRequest;
import com.auth.dto.UserResponse;
import com.auth.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Defines registration controller.
 */
@RestController
@RequestMapping("/api/v1/users")
public class RegistrationController {

    private final RegistrationService registrationService;

/**
 * Performs registration controller.
 * @param registrationService the registrationService
 */
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
/**
 * Returns save.
 * @return the result of the operation
 */
    public UserResponse save(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.fromEntity(registrationService.register(request));
    }

    @GetMapping("/availability/username")
/**
 * Returns username availability.
 * @return the result of the operation
 */
    public AvailabilityResponse usernameAvailability(@RequestParam String username) {
        return new AvailabilityResponse(registrationService.isUsernameAvailable(username));
    }

    @GetMapping("/availability/email")
/**
 * Returns email availability.
 * @return the result of the operation
 */
    public AvailabilityResponse emailAvailability(@RequestParam String email) {
        return new AvailabilityResponse(registrationService.isEmailAvailable(email));
    }
}
