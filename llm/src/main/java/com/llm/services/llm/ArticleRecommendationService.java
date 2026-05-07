package com.llm.services.llm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class ArticleRecommendationService {

    private static final int DEFAULT_TOP_K = 20;
    private static final int MAX_TOP_K = 100;

    private final JdbcTemplate jdbcTemplate;

    public ArticleRecommendationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UUID> recommendArticleIds(List<UUID> likedArticleIds, List<UUID> excludeArticleIds, Integer topK) {
        List<UUID> liked = safeList(likedArticleIds);
        if (liked.isEmpty()) {
            return List.of();
        }

        int safeTopK = Math.max(1, Math.min(topK == null ? DEFAULT_TOP_K : topK, MAX_TOP_K));
        List<UUID> excluded = new ArrayList<>(safeList(excludeArticleIds));
        excluded.addAll(liked);

        String centroid = fetchCentroidVector(liked);
        if (centroid == null || centroid.isBlank()) {
            return List.of();
        }

        String sql = """
                SELECT (metadata->>'article_id')::uuid AS article_id
                FROM vector_store
                WHERE (metadata->>'article_id') IS NOT NULL
                  AND (metadata->>'article_id')::uuid <> ALL (?::uuid[])
                ORDER BY embedding <=> CAST(? AS vector)
                LIMIT ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> (UUID) rs.getObject("article_id"),
                toPgUuidArray(excluded),
                centroid,
                safeTopK
        );
    }

    private String fetchCentroidVector(List<UUID> likedArticleIds) {
        String sql = """
                SELECT AVG(embedding)::text AS centroid
                FROM vector_store
                WHERE (metadata->>'article_id') IS NOT NULL
                  AND (metadata->>'article_id')::uuid = ANY (?::uuid[])
                """;

        return jdbcTemplate.query(
                sql,
                rs -> rs.next() ? rs.getString("centroid") : null,
                toPgUuidArray(likedArticleIds)
        );
    }

    private static List<UUID> safeList(List<UUID> ids) {
        return ids == null ? List.of() : ids.stream().filter(id -> id != null).distinct().toList();
    }

    private static Object toPgUuidArray(Collection<UUID> ids) {
        return ids.stream().map(UUID::toString).toArray(String[]::new);
    }
}
