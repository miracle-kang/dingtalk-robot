package com.code4thought.dingtalk.robot.youdao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/31
 */
@Component
public class YouDaoApi {

    private static final String API_URL = "https://openapi.youdao.com/api";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String appid;
    private final String secretKey;

    private final HttpClient httpClient;

    public YouDaoApi(@Value("youdao.twitter-bot.appid") String appid,
                     @Value("youdao.twitter-bot.secretKey") String secretKey) {
        this.appid = appid;
        this.secretKey = secretKey;
        this.httpClient = HttpClient.newHttpClient();
    }


    public String translate(String text) {
        return translate(text, "auto", "auto");
    }

    public String translate(String text, String from, String to) {

        Map<String, String> params = new HashMap<>();
        params.put("from", from);
        params.put("to", to);
        params.put("q", text);


        return null;
    }

    private TranslateData request(Map<String, String> data) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return objectMapper.readValue(response.body(), TranslateData.class);
    }
}
