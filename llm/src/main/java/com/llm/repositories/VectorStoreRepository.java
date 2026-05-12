package com.llm.repositories;

import com.llm.entities.VectorStoreDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VectorStoreRepository extends JpaRepository<VectorStoreDocument, String> {

    @Query(value = """
            SELECT AVG(embedding)::text
            FROM vector_store
            WHERE ((metadata::jsonb)->>'article_id') IS NOT NULL
              AND ((metadata::jsonb)->>'article_id')::uuid = ANY (string_to_array(:likedIdsCsv, ',')::uuid[])
            """, nativeQuery = true)
    String findCentroidByLikedIds(@Param("likedIdsCsv") String likedIdsCsv);

    @Query(value = """
            SELECT ((metadata::jsonb)->>'article_id')::uuid AS article_id
            FROM vector_store
            WHERE ((metadata::jsonb)->>'article_id') IS NOT NULL
              AND ((metadata::jsonb)->>'article_id')::uuid <> ALL (string_to_array(:excludedIdsCsv, ',')::uuid[])
            ORDER BY embedding <=> CAST(:centroid AS vector)
            LIMIT :topK
            """, nativeQuery = true)
    List<UUID> findRecommendedArticleIds(
            @Param("excludedIdsCsv") String excludedIdsCsv,
            @Param("centroid") String centroid,
            @Param("topK") int topK
    );
}
