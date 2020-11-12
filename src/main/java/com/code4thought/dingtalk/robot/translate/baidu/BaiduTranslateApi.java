package com.code4thought.dingtalk.robot.translate.baidu;

import com.code4thought.dingtalk.robot.translate.TranslateApi;
import com.code4thought.dingtalk.robot.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.concurrent.TimeUnit;


@Component
@ConditionalOnProperty(
        value = "translate.enable",
        havingValue = "baidu",
        matchIfMissing = true
)
public class BaiduTranslateApi implements TranslateApi {

    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String appId;
    private final String secretKey;

    private final HttpClient httpClient;

    public BaiduTranslateApi(@Value("${translate.baidu.appid}") String appId,
                             @Value("${translate.baidu.secretKey}") String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String translate(String text) {
        return translate(text, "auto", "zh");
    }

    @Override
    public String translate(String text, String from, String to) {
        Map<String, String> params = new HashMap<>();
        params.put("q", text);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appId);

        for (int i = 0; i < 3; i++) {
            String salt = RandomStringUtils.randomNumeric(10);
            params.put("salt", salt);

            String signStr = appId + text + salt + secretKey;
            params.put("sign", Utils.md5(signStr));

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

        JsonNode resultCode = resultNode.get("error_code");
        if (resultCode != null && resultCode.intValue() != 52000) {
            throw new RuntimeException("翻译失败: " + resultNode.get("error_msg").textValue());
        }

        JsonNode transResult = resultNode.get("trans_result");
        if (transResult == null) {
            throw new RuntimeException("翻译失败，无翻译结果");
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < transResult.size(); i++) {
            builder.append(transResult.get(i).get("dst").textValue()).append("\n");
        }
        return builder.substring(0, builder.length() - 1);
    }
}
