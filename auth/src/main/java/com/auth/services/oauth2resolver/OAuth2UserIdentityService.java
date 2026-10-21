package com.auth.services.oauth2resolver;

import com.auth.entities.CustomUserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Defines oauth2 user identity service.
 */
@Service
public class OAuth2UserIdentityService {

    private final List<OAuth2ProviderUserResolver> resolvers;

/**
 * Performs oauth2 user identity service.
 * @param resolvers the resolvers
 */
    public OAuth2UserIdentityService(List<OAuth2ProviderUserResolver> resolvers) {
        this.resolvers = resolvers;
    }

/**
 * Returns resolve.
 * @param principal the principal
 * @return the result of the operation
 */
    public CustomUserDetails resolve(OAuth2User principal) {
        for (OAuth2ProviderUserResolver resolver : resolvers) {
            if (resolver.supports(principal)) {
                return resolver.resolve(principal);
            }
        }
        return null;
    }
}
