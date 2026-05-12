package com.auth.services;

import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InternalUserService {

    private final UserRepository userRepository;

    public InternalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<CustomUserDetails> getByIds(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public CustomUserDetails getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }
}
