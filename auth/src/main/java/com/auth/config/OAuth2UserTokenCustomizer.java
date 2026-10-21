package com.auth.config;

import com.auth.entities.CustomUserDetails;
import com.auth.services.oauth2resolver.OAuth2UserIdentityService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * Defines oauth2 user token customizer.
 */
@Component
public class OAuth2UserTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final OAuth2UserIdentityService oauth2UserIdentityService;

/**
 * Performs oauth2 user token customizer.
 * @param oauth2UserIdentityService the oauth2UserIdentityService
 */
    public OAuth2UserTokenCustomizer(OAuth2UserIdentityService oauth2UserIdentityService) {
        this.oauth2UserIdentityService = oauth2UserIdentityService;
    }

    @Override
/**
 * Performs customize.
 * @param context the context
 */
    public void customize(JwtEncodingContext context) {
        Object principal = context.getPrincipal().getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            applyClaims(context, user);
            return;
        }

        if (principal instanceof OAuth2User user) {
            CustomUserDetails customUserDetails = oauth2UserIdentityService.resolve(user);
            if (customUserDetails != null) {
                applyClaims(context, customUserDetails);
            }
        }
    }

/**
 * Performs apply claims.
 * @param context the context
 * @param user the user
 */
    private void applyClaims(JwtEncodingContext context, CustomUserDetails user) {
        context.getClaims().subject(String.valueOf(user.getId()));
        context.getClaims().claim("role", user.getRole().name());
    }
}
