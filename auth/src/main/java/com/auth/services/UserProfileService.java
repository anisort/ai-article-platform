package com.auth.services;

import com.auth.dto.UserProfileResponse;
import com.auth.dto.UserSummaryResponse;
import com.auth.entities.CustomUserDetails;
import com.auth.entities.UserFollow;
import com.auth.repositories.UserFollowRepository;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    public UserProfileService(UserRepository userRepository, UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
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
