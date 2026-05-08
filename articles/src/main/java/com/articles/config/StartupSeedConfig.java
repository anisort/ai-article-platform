package com.articles.config;

import com.articles.dto.SeedArticleRequest;
import com.articles.services.ArticleFacadeService;
import com.articles.services.ArticleService;
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

    private final ArticleService articleService;
    private final ArticleFacadeService articleFacadeService;
    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String articlesFilePath;
    private final String seedToken;

    public StartupSeedConfig(
            ArticleService articleService,
            ArticleFacadeService articleFacadeService,
            ObjectMapper objectMapper,
            @Value("${app.seed.on-startup:false}") boolean enabled,
            @Value("${app.seed.articles-file:../seed/data/articles.seed.json}") String articlesFilePath,
            @Value("${app.seed.token:}") String seedToken
    ) {
        this.articleService = articleService;
        this.articleFacadeService = articleFacadeService;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.articlesFilePath = articlesFilePath;
        this.seedToken = seedToken;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedOnStartup() {
        if (!enabled) {
            return;
        }

        try {
            long existingArticles = articleService.count();
            if (existingArticles > 0) {
                log.info("Startup seed skipped for articles: article table already has {} records", existingArticles);
                return;
            }

            Path file = Path.of(articlesFilePath).normalize();
            if (!Files.exists(file)) {
                log.warn("Startup seed skipped for articles: file not found at {}", file);
                return;
            }

            List<SeedArticleRequest> articles = objectMapper.readValue(
                    stripBom(Files.readString(file)),
                    new TypeReference<List<SeedArticleRequest>>() {
                    }
            );
            if (articles.isEmpty()) {
                log.warn("Startup seed skipped for articles: file {} is empty", file);
                return;
            }

            articleFacadeService.seedArticles(articles, seedToken);
            log.info("Startup seed completed for articles: created {} articles", articles.size());
        } catch (Exception ex) {
            log.error("Startup seed failed for articles", ex);
        }
    }

    private String stripBom(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }
}
