package com.example.myth.controller;

import com.example.myth.entity.dto.AiResult;
import com.example.myth.entity.dto.ContentDto;
import com.example.myth.util.JsonUtils;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
public class AiController {
    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    private static final String DONE = "[DONE]";
    private static final Integer timeout = 60;

//    private static final String AI_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";
    private static final String LOCAL_AI_URL = "http://localhost:9621/query";

    @Value("${api.password:}")
    private String apiPassword;

    @GetMapping("/api/stream")
    public void handleSse(String message, HttpServletResponse response) {
        System.out.println(message);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter pw = response.getWriter()) {
            getAiResult(pw, message);
            pw.write("data:end\n\n");
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAiResult(PrintWriter pw, String content) throws InterruptedException {
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
                    }
                }
            }
        } catch (Exception e) {
            logger.error("调用本地AI失败", e);
            pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto("系统错误，请稍后重试")) + "\n\n");
            pw.flush();
        }
    }

    private static String getContent(String data) {
        AiResult aiResult = JsonUtils.convertJson2Obj(data, AiResult.class);
        return aiResult.getChoices().get(0).getDelta().getContent();
    }
}
