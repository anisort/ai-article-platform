package com.auth.services;

import com.auth.dto.CreateUserRequest;
import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomUserDetails register(CreateUserRequest request) {
        CustomUserDetails user = new CustomUserDetails();
        request.toUser(user);
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
