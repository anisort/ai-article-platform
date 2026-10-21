package com.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Defines user follow.
 */
@Getter
@Setter
@Entity
@IdClass(UserFollowId.class)
@Table(name = "user_follow")
public class UserFollow {

    @Id
    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Id
    @Column(name = "following_id", nullable = false)
    private UUID followingId;
}
