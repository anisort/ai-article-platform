package com.llm.repositories;

import com.llm.entities.ArticleAssistantChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleAssistantChatRepository extends JpaRepository<ArticleAssistantChat, UUID> {
    List<ArticleAssistantChat> findByArticleIdOrderByCreatedAtDesc(UUID articleId);
}
