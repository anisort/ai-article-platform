package com.auth.services.oauth2resolver;

import com.auth.entities.CustomUserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Declares oauth2 provider user resolver.
 */
public interface OAuth2ProviderUserResolver {
    boolean supports(OAuth2User principal);

    CustomUserDetails resolve(OAuth2User principal);
}
