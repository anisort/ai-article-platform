package com.articles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines security config.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
/**
 * Returns filter chain.
 * @param http the http
 * @return the result of the operation
 */
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/edit-page").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/me/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/*/images").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/articles/*/images/*").authenticated()
                        .requestMatchers("/api/v1/profile-page/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profile-page/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/search/**").permitAll()
                        .requestMatchers("/api/v1/articles/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
/**
 * Returns jwt authentication converter.
 * @return the result of the operation
 */
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            String role = jwt.getClaimAsString("role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            return authorities;
        });
        return converter;
    }

}
