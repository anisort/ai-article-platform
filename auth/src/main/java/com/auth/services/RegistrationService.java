package com.auth.services;

import com.auth.dto.CreateUserRequest;
import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Defines registration service.
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

/**
 * Performs registration service.
 * @param userRepository the userRepository
 * @param passwordEncoder the passwordEncoder
 */
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
/**
 * Returns register.
 * @param request the request
 * @return the result of the operation
 */
    public CustomUserDetails register(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already taken"
            );
        }

        CustomUserDetails user = new CustomUserDetails();
        request.toUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
/**
 * Returns is username available.
 * @param username the username
 * @return the result of the operation
 */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
/**
 * Returns is email available.
 * @param email the email
 * @return the result of the operation
 */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
