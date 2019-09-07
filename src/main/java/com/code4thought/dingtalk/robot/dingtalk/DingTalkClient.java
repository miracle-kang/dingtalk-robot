package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 钉钉机器人客户端
 *
 * @author kangliqi
 * @date 2019/8/15
 */
public class DingTalkClient {

    private static final String DING_TALK_API = "https://oapi.dingtalk.com/robot/send?access_token=";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String accessToken;
    private final HttpClient httpClient;

    public DingTalkClient(String accessToken) {
        this.accessToken = accessToken;
        httpClient = HttpClient.newHttpClient();
    }

    public void sendTextMessage(TextMessage textMessage) {
        sendMessage(textMessage).validate();
    }

    public void sendMarkdownMessage(MarkdownMessage markdownMessage) {
        sendMessage(markdownMessage).validate();
    }

    private DingTalkResponse sendMessage(IDingTalkMessage message) {
        try {
            HttpResponse<byte[]> response = httpClient
                    .send(buildHttpPost(message), HttpResponse.BodyHandlers.ofByteArray());
            return objectMapper.readValue(response.body(), DingTalkResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("请求DingTalk API异常！");
        } catch (InterruptedException e) {
            throw new RuntimeException("操作中断");
        }
    }

    private HttpRequest buildHttpPost(Object data) throws JsonProcessingException {
        return HttpRequest.newBuilder()
                .uri(URI.create(DING_TALK_API + accessToken))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
                .build();
    }
}
