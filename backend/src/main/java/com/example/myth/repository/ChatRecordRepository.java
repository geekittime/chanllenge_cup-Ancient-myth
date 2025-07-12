package com.example.myth.repository;

import com.example.myth.entity.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {

    @Query("SELECT r FROM ChatRecord r WHERE r.sessionId = :sessionId ORDER BY r.createTime ASC")
    List<ChatRecord> findBySessionIdOrderByCreateTimeAsc(@Param("sessionId") Long sessionId);
}