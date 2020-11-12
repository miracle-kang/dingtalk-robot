package com.code4thought.dingtalk.robot.translate.youdao;

import com.code4thought.dingtalk.robot.translate.TranslateApi;
import com.code4thought.dingtalk.robot.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/31
 */
@Component
@ConditionalOnProperty(
        value = "translate.enable",
        havingValue = "youdao"
)
public class YouDaoTranslateApi implements TranslateApi {

    private static final String API_URL = "https://openapi.youdao.com/api";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String appid;
    private final String secretKey;

    private final HttpClient httpClient;

    public YouDaoTranslateApi(@Value("${translate.youdao.appid}") String appid,
                              @Value("${translate.youdao.secretKey}") String secretKey) {
        this.appid = appid;
        this.secretKey = secretKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String translate(String text) {
        return translate(text, "auto", "auto");
    }

    @Override
    public String translate(String text, String from, String to) {

        Map<String, String> params = new HashMap<>();
        params.put("from", from);
        params.put("to", to);
        params.put("q", text);
        params.put("appKey", appid);

        for (int i = 0; i < 3; i++) {
            String salt = UUID.randomUUID().toString();
            params.put("salt", salt);
            params.put("signType", "v3");

            String curtime = String.valueOf(System.currentTimeMillis() / 1000);
            params.put("curtime", curtime);
            params.put("secureKey", secretKey);

            String signStr = appid + truncate(text) + salt + curtime + secretKey;
            params.put("sign", Utils.SHA256(signStr));

            try {
                return request(params);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "";
    }

    private String request(Map<String, String> data) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(Utils.paramToUrlEncoded(data)))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        JsonNode resultNode = objectMapper.readTree(response.body());

        String resultCode = resultNode.get("errorCode").textValue();
        if (resultCode == null || !resultCode.equals("0")) {
            throw new RuntimeException("翻译失败: " + resultCode);
        }

        return resultNode.get("translation").get(0).textValue();
    }

    private String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}
