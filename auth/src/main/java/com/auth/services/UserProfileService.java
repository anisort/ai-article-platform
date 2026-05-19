package com.auth.services;

import com.auth.dto.UserProfileResponse;
import com.auth.dto.UserBriefResponse;
import com.auth.dto.ToggleFollowResponse;
import com.auth.entities.CustomUserDetails;
import com.auth.entities.UserFollow;
import com.auth.repositories.UserFollowRepository;
import com.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final FileStorageService fileStorageService;

    public UserProfileService(UserRepository userRepository,
                              UserFollowRepository userFollowRepository,
                              FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.fileStorageService = fileStorageService;
    }

    public UserProfileResponse getMe(UUID userId) {
        return getProfile(userId, userId);
    }

    public UserProfileResponse getProfile(UUID userId, UUID viewerId) {
        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        boolean ownProfile = userId.equals(viewerId);
        boolean following = viewerId != null && userFollowRepository.existsByFollowerIdAndFollowingId(viewerId, userId);

        List<UserBriefResponse> followers = ownProfile
                ? getFollowUsers(userFollowRepository.findByFollowingId(userId), UserFollow::getFollowerId)
                : List.of();
        List<UserBriefResponse> followingUsers = ownProfile
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
    public UserProfileResponse updateMe(UUID userId, String username, String name, MultipartFile avatar) {
        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        if (StringUtils.hasText(username)) {
            String normalizedUsername = username.trim();
            if (userRepository.existsByUsernameAndIdNot(normalizedUsername, userId)) {
                throw new ResponseStatusException(CONFLICT, "Username is already taken");
            }
            user.setUsername(normalizedUsername);
        }

        if (StringUtils.hasText(name)) {
            user.setName(name.trim());
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = fileStorageService.upload(avatar);
            user.setAvatar(avatarUrl);
        }

        CustomUserDetails updated = userRepository.save(user);
        return new UserProfileResponse(
                updated.getId(),
                updated.getUsername(),
                updated.getName(),
                updated.getEmail(),
                updated.getAvatar(),
                true,
                false,
                getFollowUsers(userFollowRepository.findByFollowingId(userId), UserFollow::getFollowerId),
                getFollowUsers(userFollowRepository.findByFollowerId(userId), UserFollow::getFollowingId)
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

    private List<UserBriefResponse> getFollowUsers(List<UserFollow> follows, Function<UserFollow, UUID> idExtractor) {
        List<UUID> ids = follows.stream()
                .map(idExtractor)
                .distinct()
                .toList();

        Map<UUID, CustomUserDetails> usersById = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(CustomUserDetails::getId, Function.identity()));

        return ids.stream()
                .map(usersById::get)
                .filter(Objects::nonNull)
                .map(UserBriefResponse::fromEntity)
                .sorted(Comparator.comparing(UserBriefResponse::name, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(UserBriefResponse::username, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }
}
