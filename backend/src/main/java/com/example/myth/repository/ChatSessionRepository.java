package com.example.myth.repository;

import com.example.myth.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    @Query("SELECT s FROM ChatSession s WHERE s.userId = :userId ORDER BY s.updateTime DESC")
    List<ChatSession> findByUserIdOrderByUpdateTimeDesc(@Param("userId") String userId);

    @Query("SELECT COUNT(s) FROM ChatSession s WHERE s.userId = :userId")
    Long countByUserId(@Param("userId") String userId);
}