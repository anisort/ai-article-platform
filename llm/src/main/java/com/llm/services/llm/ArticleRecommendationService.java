package com.llm.services.llm;

import com.llm.repositories.VectorStoreRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleRecommendationService {

    private static final int DEFAULT_TOP_K = 20;
    private static final int MAX_TOP_K = 100;

    private final VectorStoreRepository vectorStoreRepository;

    public ArticleRecommendationService(VectorStoreRepository vectorStoreRepository) {
        this.vectorStoreRepository = vectorStoreRepository;
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

        return vectorStoreRepository.findRecommendedArticleIds(
                toCsvUuidList(excluded),
                centroid,
                safeTopK
        );
    }

    private String fetchCentroidVector(List<UUID> likedArticleIds) {
        return vectorStoreRepository.findCentroidByLikedIds(toCsvUuidList(likedArticleIds));
    }

    private static List<UUID> safeList(List<UUID> ids) {
        return ids == null ? List.of() : ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private static String toCsvUuidList(Collection<UUID> ids) {
        return ids.stream().map(UUID::toString).collect(Collectors.joining(","));
    }
}
