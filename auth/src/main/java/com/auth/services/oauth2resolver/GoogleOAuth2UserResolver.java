package com.auth.services.oauth2resolver;

import com.auth.entities.CustomUserDetails;
import com.auth.entities.Role;
import com.auth.repositories.UserRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuth2UserResolver implements OAuth2ProviderUserResolver {

    private final UserRepository userRepository;

    public GoogleOAuth2UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(OAuth2User principal) {
        return principal instanceof DefaultOidcUser;
    }

    @Override
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
