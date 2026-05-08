package com.auth.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.auth.entities.Role;
import com.auth.repositories.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import com.auth.entities.CustomUserDetails;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.client.id}")
    private String clientId;

    @Value("${auth.client.secret}")
    private String clientSecret;

    @Value("${auth.client.redirect-uri}")
    private String clientRedirectUri;

    @Value("${auth.issuer-uri}")
    private String issuerUri;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .oauth2AuthorizationServer((authorizationServer) -> {
                    http.securityMatcher(authorizationServer.getEndpointsMatcher()).cors(Customizer.withDefaults());
                    authorizationServer
                            .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
                })
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/.well-known/appspecific/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/seed").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/follow").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/batch").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientAuthenticationMethods(methods -> {
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                })
                .clientSecret(passwordEncoder().encode(clientSecret))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(clientRedirectUri)
                .postLogoutRedirectUri(clientRedirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(true)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository) {
        return context -> {
            Object principal = context.getPrincipal().getPrincipal();
            if (principal instanceof CustomUserDetails user) {
                context.getClaims().subject(String.valueOf(user.getId()));
                context.getClaims().claim("role", user.getRole().name());
            }
            else if (principal instanceof DefaultOidcUser user) {
                CustomUserDetails customUserDetails = userRepository.findByGoogleSub(user.getSubject())
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

                context.getClaims().subject(String.valueOf(customUserDetails.getId()));
                context.getClaims().claim("role", customUserDetails.getRole().name());
            }
            else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User user) {

                Object id = user.getAttribute("id"); // github id
                if (id == null) return;

                String githubSub = id.toString();
                String email = user.getAttribute("email");
                String login = user.getAttribute("login");
                String name = user.getAttribute("name");
                String avatar = user.getAttribute("avatar_url");

                CustomUserDetails custom = userRepository.findByGithubSub(githubSub)
                        .orElseGet(() -> {

                            if (email != null) {
                                return userRepository.findByEmail(email)
                                        .map(existing -> {
                                            existing.setGithubSub(githubSub);
                                            return userRepository.save(existing);
                                        })
                                        .orElseGet(() -> createGithubUser(githubSub, email, login, name, avatar, userRepository));
                            }


                            return createGithubUser(githubSub, null, login, name, avatar, userRepository);
                        });

                context.getClaims().subject(String.valueOf(custom.getId()));
                context.getClaims().claim("role", custom.getRole().name());
            }
        };
    }

    private static CustomUserDetails createGithubUser(
            String githubSub,
            String email,
            String login,
            String name,
            String avatar,
            UserRepository userRepository
    ) {
        CustomUserDetails nu = new CustomUserDetails();
        nu.setGithubSub(githubSub);
        nu.setEmail(email);
        nu.setUsername(login != null ? login : "github-" + githubSub);
        nu.setName(name);
        nu.setAvatar(avatar);
        nu.setRole(Role.USER);
        return userRepository.save(nu);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuerUri)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
