package com.auth.config;

import com.auth.dto.SeedUserRequest;
import com.auth.repositories.UserRepository;
import com.auth.services.UserService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class StartupSeedConfig {

    private static final Logger log = LoggerFactory.getLogger(StartupSeedConfig.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String usersFilePath;
    private final String seedToken;

    public StartupSeedConfig(
            UserService userService,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            @Value("${app.seed.on-startup:false}") boolean enabled,
            @Value("${app.seed.users-file:../seed/data/users.seed.json}") String usersFilePath,
            @Value("${app.seed.token:}") String seedToken
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.usersFilePath = usersFilePath;
        this.seedToken = seedToken;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedOnStartup() {
        if (!enabled) {
            return;
        }

        try {
            long existingUsers = userRepository.count();
            if (existingUsers > 0) {
                log.info("Startup seed skipped for auth: users table already has {} records", existingUsers);
                return;
            }

            Path file = Path.of(usersFilePath).normalize();
            if (!Files.exists(file)) {
                log.warn("Startup seed skipped for auth: users file not found at {}", file);
                return;
            }

            List<SeedUserRequest> users = objectMapper.readValue(
                    stripBom(Files.readString(file)),
                    new TypeReference<List<SeedUserRequest>>() {
                    }
            );
            if (users.isEmpty()) {
                log.warn("Startup seed skipped for auth: users file {} is empty", file);
                return;
            }

            userService.seedUsers(users, seedToken);
            log.info("Startup seed completed for auth: created {} users", users.size());
        } catch (Exception ex) {
            log.error("Startup seed failed for auth", ex);
        }
    }

    private String stripBom(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }
}
