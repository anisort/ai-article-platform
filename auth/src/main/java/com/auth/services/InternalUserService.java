package com.auth.services;

import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InternalUserService {

    private final UserRepository userRepository;

    public InternalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<CustomUserDetails> getByIds(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }
}
