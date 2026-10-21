package com.auth.services;

import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Defines internal user service.
 */
@Service
public class InternalUserService {

    private final UserRepository userRepository;

/**
 * Performs internal user service.
 * @param userRepository the userRepository
 */
    public InternalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

/**
 * Returns get by ids.
 * @param ids the ids
 * @return the result of the operation
 */
    public List<CustomUserDetails> getByIds(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }
}
