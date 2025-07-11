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

    private static final String AI_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";

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
        Map<String, Object> params = new HashMap<>();
        params.put("model", "4.0Ultra");
        //result.put("user", "4.0Ultra");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        content="你是一个精通人性的女讲师，你的说话内容要符合你的气质，你要牢记自己的身份 "+content;
        message.put("content", content);

        List<Map> messages = new ArrayList<>();
        messages.add(message);
        params.put("messages", messages);
        params.put("stream", true);
        String jsonParams = JsonUtils.convertObj2Json(params);

        Request.Builder builder = new Request.Builder().url(AI_URL);
        builder.addHeader("Authorization", " Bearer " + apiPassword);
        builder.addHeader("Accept", "text/event-stream");
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = builder.post(body).build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout,
                TimeUnit.SECONDS).build();

        // 实例化EventSource，注册EventSource监听器 -- 创建一个用于处理服务器发送事件的实例，并定义处理事件的回调逻辑
        CountDownLatch eventLatch = new CountDownLatch(1);

        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (DONE.equals(data)) {
                    return;
                }
                String content = getContent(data);
                pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(content)) + "\n\n");
                pw.flush();
            }

            @Override
            public void onClosed(EventSource eventSource) {
                super.onClosed(eventSource);
                eventLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                logger.info("调用接口失败{}", t);
                if (eventLatch != null) {
                    eventLatch.countDown();
                }
            }
        });
        // 与服务器建立连接
        realEventSource.connect(client);
        // await() 方法被调用来阻塞当前线程，直到 CountDownLatch 的计数变为0。
        eventLatch.await();
    }

    private static String getContent(String data) {
        AiResult aiResult = JsonUtils.convertJson2Obj(data, AiResult.class);
        return aiResult.getChoices().get(0).getDelta().getContent();
    }
}
