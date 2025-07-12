package com.example.myth.controller;

import com.example.myth.entity.ChatRecord;
import com.example.myth.entity.ChatSession;
import com.example.myth.entity.dto.AiResult;
import com.example.myth.entity.dto.ContentDto;
import com.example.myth.service.serviceImpl.ChatService;
import com.example.myth.util.JsonUtils;
import okhttp3.*;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
public class AiController {
    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    private static final String DONE = "[DONE]";
    private static final Integer timeout = 60;
    private static final String LOCAL_AI_URL = "http://localhost:9621/query";

    @Autowired
    private ChatService chatService;

    @Value("${api.password:}")
    private String apiPassword;

    @GetMapping("/api/stream")
    public void handleSse(@RequestParam String message,
                          @RequestParam Long sessionId,
                          @RequestParam(defaultValue = "user") String userId,
                          HttpServletResponse response) {
        System.out.println("Message: " + message + ", SessionId: " + sessionId);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");

        try (PrintWriter pw = response.getWriter()) {
            // 保存用户消息
            chatService.saveChatRecord(sessionId, 0, message);

            // 获取AI响应
            String aiResponse = getAiResult(pw, message);

            // 保存AI消息
            if (aiResponse != null && !aiResponse.isEmpty()) {
                chatService.saveChatRecord(sessionId, 1, aiResponse);
            }

            pw.write("data:end\n\n");
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAiResult(PrintWriter pw, String content) throws InterruptedException {
        try {
            // 构建本地AI请求参数
            Map<String, Object> params = new HashMap<>();
            String enhancedContent = "你的角色是一个上古神话小助手，你很擅长上古神话相关的知识和故事 " + content;
            params.put("query", enhancedContent);
            params.put("mode", "hybrid");

            String jsonParams = JsonUtils.convertObj2Json(params);

            // 创建HTTP请求
            Request.Builder builder = new Request.Builder().url(LOCAL_AI_URL);
            builder.addHeader("Content-Type", "application/json");
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
            Request request = builder.post(body).build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .build();

            // 发送请求并获取响应
            try (Response httpResponse = client.newCall(request).execute()) {
                if (httpResponse.isSuccessful() && httpResponse.body() != null) {
                    String responseBody = httpResponse.body().string();
                    Map<String, Object> responseMap = JsonUtils.convertJson2Obj(responseBody, Map.class);
                    String aiResponse = (String) responseMap.get("response");

                    // 直接发送完整响应
                    if (aiResponse != null && !aiResponse.isEmpty()) {
                        pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(aiResponse)) + "\n\n");
                        pw.flush();
                        return aiResponse;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("调用本地AI失败", e);
            String errorMsg = "系统错误，请稍后重试";
            pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(errorMsg)) + "\n\n");
            pw.flush();
            return errorMsg;
        }
        return null;
    }

    /**
     * 创建新的对话会话
     */
    @PostMapping("/api/chat/session")
    public ResponseEntity<ChatSession> createSession(@RequestParam(defaultValue = "user") String userId) {
        ChatSession session = chatService.createNewSession(userId);
        return ResponseEntity.ok(session);
    }

    /**
     * 获取用户的对话会话列表
     */
    @GetMapping("/api/chat/sessions")
    public ResponseEntity<List<ChatSession>> getUserSessions(@RequestParam(defaultValue = "user") String userId) {
        List<ChatSession> sessions = chatService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 获取指定会话的聊天记录
     */
    @GetMapping("/api/chat/records/{sessionId}")
    public ResponseEntity<List<ChatRecord>> getSessionRecords(@PathVariable Long sessionId) {
        List<ChatRecord> records = chatService.getSessionRecords(sessionId);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/api/chat/session/{sessionId}")
    public ResponseEntity<ChatSession> getSession(@PathVariable Long sessionId) {
        Optional<ChatSession> session = chatService.getSessionById(sessionId);
        if (session.isPresent()) {
            return ResponseEntity.ok(session.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static String getContent(String data) {
        AiResult aiResult = JsonUtils.convertJson2Obj(data, AiResult.class);
        return aiResult.getChoices().get(0).getDelta().getContent();
    }
}