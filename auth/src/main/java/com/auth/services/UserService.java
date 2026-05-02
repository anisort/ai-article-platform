package com.auth.services;

import com.auth.dto.CreateUserRequest;
import com.auth.dto.ToggleFollowResponse;
import com.auth.dto.UserProfileResponse;
import com.auth.dto.UserSummaryResponse;
import com.auth.entities.CustomUserDetails;
import com.auth.entities.UserFollow;
import com.auth.repositories.UserFollowRepository;
import com.auth.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserFollowRepository userFollowRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomUserDetails save(CreateUserRequest request) {
        CustomUserDetails user = new CustomUserDetails();
        request.toUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<CustomUserDetails> getByIds(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public CustomUserDetails getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public UserProfileResponse getMe(UUID userId) {
        return getProfile(userId, userId);
    }

    public UserProfileResponse getProfile(UUID userId, UUID viewerId) {
        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        boolean ownProfile = userId.equals(viewerId);
        boolean following = viewerId != null && userFollowRepository.existsByFollowerIdAndFollowingId(viewerId, userId);

        List<UserSummaryResponse> followers = ownProfile
                ? getFollowUsers(userFollowRepository.findByFollowingId(userId), UserFollow::getFollowerId)
                : List.of();
        List<UserSummaryResponse> followingUsers = ownProfile
                ? getFollowUsers(userFollowRepository.findByFollowerId(userId), UserFollow::getFollowingId)
                : List.of();

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                ownProfile ? user.getEmail() : null,
                user.getAvatar(),
                ownProfile,
                following,
                followers,
                followingUsers
        );
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

    private List<UserSummaryResponse> getFollowUsers(List<UserFollow> follows, Function<UserFollow, UUID> idExtractor) {
        List<UUID> ids = follows.stream()
                .map(idExtractor)
                .distinct()
                .toList();

        Map<UUID, CustomUserDetails> usersById = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(CustomUserDetails::getId, Function.identity()));

        return ids.stream()
                .map(usersById::get)
                .filter(Objects::nonNull)
                .map(UserSummaryResponse::fromEntity)
                .sorted(Comparator.comparing(UserSummaryResponse::name, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(UserSummaryResponse::username, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }
}
