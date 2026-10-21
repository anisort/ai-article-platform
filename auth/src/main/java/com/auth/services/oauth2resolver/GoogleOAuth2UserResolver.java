package com.auth.services.oauth2resolver;

import com.auth.entities.CustomUserDetails;
import com.auth.entities.Role;
import com.auth.repositories.UserRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * Defines google oauth2 user resolver.
 */
@Component
public class GoogleOAuth2UserResolver implements OAuth2ProviderUserResolver {

    private final UserRepository userRepository;

/**
 * Performs google oauth2 user resolver.
 * @param userRepository the userRepository
 */
    public GoogleOAuth2UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
/**
 * Returns supports.
 * @param principal the principal
 * @return the result of the operation
 */
    public boolean supports(OAuth2User principal) {
        return principal instanceof DefaultOidcUser;
    }

    @Override
/**
 * Returns resolve.
 * @param principal the principal
 * @return the result of the operation
 */
    public CustomUserDetails resolve(OAuth2User principal) {
        DefaultOidcUser user = (DefaultOidcUser) principal;
        return userRepository.findByGoogleSub(user.getSubject())
                .orElseGet(() -> userRepository.findByEmail(user.getEmail())
                        .map(existingUser -> {
                            existingUser.setGoogleSub(user.getSubject());
                            return userRepository.save(existingUser);
                        })
                        .orElseGet(() -> {
                            CustomUserDetails newUser = new CustomUserDetails();
                            newUser.setGoogleSub(user.getSubject());
                            newUser.setEmail(user.getEmail());
                            newUser.setName(user.getFullName());
                            newUser.setAvatar(user.getPicture());
                            newUser.setRole(Role.USER);
                            return userRepository.save(newUser);
                        }));
    }
}
