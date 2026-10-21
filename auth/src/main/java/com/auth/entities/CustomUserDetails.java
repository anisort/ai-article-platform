package com.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Defines custom user details.
 */
@Setter
@Entity
@Table(name = "users")
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;

    @Getter
    private String email;

    private String password;

    @Getter
    private Role role;

    @Getter
    private String avatar;

    @Getter
    private String googleSub;

    @Getter
    private String githubSub;

    @Getter
    private String name;

    @Override
/**
 * Returns get username.
 * @return the result of the operation
 */
    public String getUsername() {
        return this.username;
    }

    @Override
/**
 * Returns get password.
 * @return the result of the operation
 */
    public String getPassword() {
        return this.password;
    }

    @Override
/**
 * Returns get authorities.
 * @return the result of the operation
 */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
/**
 * Performs erase credentials.
 */
    public void eraseCredentials() {
        this.password = null;
    }
}
