package com.auth.api;

import com.auth.dto.CreateUserRequest;
import com.auth.dto.UserResponse;
import com.auth.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse save(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.fromEntity(userService.save(request));
    }

    @PostMapping("/batch")
    public List<UserResponse> batch(@RequestBody List<UUID> ids) {
        return userService.getByIds(ids).stream().map(UserResponse::fromEntity).toList();
    }
}
