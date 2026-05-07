package com.auth.repositories;

import com.auth.entities.UserFollow;
import com.auth.entities.UserFollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollowId> {
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    List<UserFollow> findByFollowerId(UUID followerId);
    List<UserFollow> findByFollowingId(UUID followingId);
    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
}
