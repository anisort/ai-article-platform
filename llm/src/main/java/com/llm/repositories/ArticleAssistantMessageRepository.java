package com.llm.repositories;

import com.llm.entities.ArticleAssistantChat;
import com.llm.entities.ArticleAssistantMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleAssistantMessageRepository extends JpaRepository<ArticleAssistantMessage, UUID> {
    List<ArticleAssistantMessage> findByChatOrderByCreatedAtAsc(ArticleAssistantChat chat);
}
