package com.auth.services;

import com.auth.dto.CreateUserRequest;
import com.auth.entities.CustomUserDetails;
import com.auth.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository/*, PasswordEncoder passwordEncoder*/) {
        this.userRepository = userRepository;
        //this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomUserDetails save(CreateUserRequest request) {
        CustomUserDetails user = new CustomUserDetails();
        request.toUser(user);
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<CustomUserDetails> getByIds(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }
}
