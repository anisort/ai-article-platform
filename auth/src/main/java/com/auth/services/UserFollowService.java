package com.auth.services;

import com.auth.dto.ToggleFollowResponse;
import com.auth.entities.UserFollow;
import com.auth.repositories.UserFollowRepository;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserFollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    public UserFollowService(UserRepository userRepository, UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
    }

    @Transactional
    public ToggleFollowResponse toggleFollow(UUID followerId, UUID targetUserId) {
        if (followerId.equals(targetUserId)) {
            throw new ResponseStatusException(BAD_REQUEST, "You cannot follow yourself");
        }

        userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        boolean alreadyFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(followerId, targetUserId);
        if (alreadyFollowing) {
            userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, targetUserId);
            return new ToggleFollowResponse(targetUserId, false);
        }

        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(targetUserId);
        userFollowRepository.save(follow);
        return new ToggleFollowResponse(targetUserId, true);
    }
}
