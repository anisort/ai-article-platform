package com.auth.repositories;

import com.auth.entities.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<CustomUserDetails, UUID> {
    Optional<CustomUserDetails> findByUsername(String username);
    Optional<CustomUserDetails> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<CustomUserDetails> findByGoogleSub(String googleSub);
    Optional<CustomUserDetails> findByGithubSub(String githubSub);
}
