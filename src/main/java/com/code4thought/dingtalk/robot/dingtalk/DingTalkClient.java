package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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

    private final String secret;
    private final String accessToken;
    private final HttpClient httpClient;

    public DingTalkClient(String accessToken, String secret) {
        Validate.notNull(accessToken, "Access token must not be null.");
        Validate.notNull(accessToken, "secret must not be null.");

        this.secret = secret;
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
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        String sign;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败！");
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(DING_TALK_API + accessToken + "&timestamp=" + timestamp + "&sign=" + sign))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
                .build();
    }
}
