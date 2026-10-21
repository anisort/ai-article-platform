package com.auth.services.oauth2resolver;

import com.auth.entities.CustomUserDetails;
import com.auth.entities.Role;
import com.auth.repositories.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * Defines github oauth2 user resolver.
 */
@Component
public class GithubOAuth2UserResolver implements OAuth2ProviderUserResolver {

    private final UserRepository userRepository;

/**
 * Performs github oauth2 user resolver.
 * @param userRepository the userRepository
 */
    public GithubOAuth2UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
/**
 * Returns supports.
 * @param principal the principal
 * @return the result of the operation
 */
    public boolean supports(OAuth2User principal) {
        Object id = principal.getAttribute("id");
        return id != null;
    }

    @Override
/**
 * Returns resolve.
 * @param principal the principal
 * @return the result of the operation
 */
    public CustomUserDetails resolve(OAuth2User principal) {
        Object id = principal.getAttribute("id");
        if (id == null) {
            return null;
        }

        String githubSub = id.toString();
        String email = principal.getAttribute("email");
        String login = principal.getAttribute("login");
        String name = principal.getAttribute("name");
        String avatar = principal.getAttribute("avatar_url");

        return userRepository.findByGithubSub(githubSub)
                .orElseGet(() -> {
                    if (email != null) {
                        return userRepository.findByEmail(email)
                                .map(existing -> {
                                    existing.setGithubSub(githubSub);
                                    return userRepository.save(existing);
                                })
                                .orElseGet(() -> createGithubUser(githubSub, email, login, name, avatar));
                    }
                    return createGithubUser(githubSub, null, login, name, avatar);
                });
    }

    private CustomUserDetails createGithubUser(
            String githubSub,
            String email,
            String login,
            String name,
            String avatar
    ) {
        CustomUserDetails user = new CustomUserDetails();
        user.setGithubSub(githubSub);
        user.setEmail(email);
        user.setUsername(login != null ? login : "github-" + githubSub);
        user.setName(name);
        user.setAvatar(avatar);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
}
