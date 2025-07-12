package com.example.myth.service.serviceImpl;

import com.example.myth.entity.ChatRecord;
import com.example.myth.entity.ChatSession;
import com.example.myth.repository.ChatRecordRepository;
import com.example.myth.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    /**
     * 创建新的对话会话
     */
    @Transactional
    public ChatSession createNewSession(String userId) {
        Long sessionCount = chatSessionRepository.countByUserId(userId);
        String sessionName = "对话" + (sessionCount + 1);

        ChatSession session = new ChatSession(userId, sessionName);
        return chatSessionRepository.save(session);
    }

    /**
     * 获取用户的所有对话会话
     */
    public List<ChatSession> getUserSessions(String userId) {
        return chatSessionRepository.findByUserIdOrderByUpdateTimeDesc(userId);
    }

    /**
     * 保存聊天记录
     */
    @Transactional
    public ChatRecord saveChatRecord(Long sessionId, Integer messageType, String content) {
        ChatRecord record = new ChatRecord(sessionId, messageType, content);
        ChatRecord savedRecord = chatRecordRepository.save(record);

        // 更新会话的更新时间
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            session.setUpdateTime(LocalDateTime.now());
            chatSessionRepository.save(session);
        }

        return savedRecord;
    }

    /**
     * 获取指定会话的聊天记录
     */
    public List<ChatRecord> getSessionRecords(Long sessionId) {
        return chatRecordRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    /**
     * 获取指定会话信息
     */
    public Optional<ChatSession> getSessionById(Long sessionId) {
        return chatSessionRepository.findById(sessionId);
    }
}