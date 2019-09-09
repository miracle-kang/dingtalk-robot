package com.code4thought.dingtalk.robot.api;

import com.code4thought.dingtalk.robot.api.form.MessageForm;
import com.code4thought.dingtalk.robot.api.form.TweetForm;
import com.code4thought.dingtalk.robot.dingtalk.DingTalkClient;
import com.code4thought.dingtalk.robot.dingtalk.MarkdownMessage;
import com.code4thought.dingtalk.robot.dingtalk.MessageAt;
import com.code4thought.dingtalk.robot.dingtalk.TextMessage;
import com.code4thought.dingtalk.robot.youdao.YouDaoApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/31
 */
@RestController
@RequestMapping("/")
public class WebResources {

    public static Logger logger = LoggerFactory.getLogger(WebResources.class);

    private final YouDaoApi youDaoApi;
    private final ObjectMapper objectMapper;

    public WebResources(YouDaoApi youDaoApi) {
        this.youDaoApi = youDaoApi;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping(
            value = "/dingTalk/{token}/tweetMessage",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void newTweet(@PathVariable String token, @RequestParam Map<String, String> params) {

        TweetForm form = objectMapper.convertValue(params, TweetForm.class);

        logger.info("{} new tweeted: {}", form.getUsername(), form);

        String translatedText = youDaoApi.translate(form.getText());
        logger.info("Translated Text: {}", translatedText);

        DingTalkClient dingTalkClient = new DingTalkClient(token);
        dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                "@" + form.getUsername() + " tweeted",
                "#### " + form.getText() + "\n"
                        + "##### 译文\n"
                        + "#### " + translatedText + "\n"
                        + "##### [原文链接](" + form.getLinkToTweet() + ")\n"
                        + "###### " + form.getCreatedAt(),
                MessageAt.atAll()));

        logger.info("Send ding talk message result OK.");
    }

    @PostMapping(
            value = "/dingTalk/{token}/message",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void newMessage(@PathVariable String token, @RequestParam Map<String, String> params) {

        MessageForm form = objectMapper.convertValue(params, MessageForm.class);

        logger.info("New message {}", form);

        String translatedText = null;
        if (form.getTranslate() != null && form.getTranslate()) {
            translatedText = youDaoApi.translate(form.getText());
        }

        DingTalkClient dingTalkClient = new DingTalkClient(token);
        MessageAt messageAt = form.getAtAll() != null && form.getAtAll() ? MessageAt.atAll() : null;
        if (translatedText == null && (form.getTitle() == null || form.getTitle().isBlank())) {
            dingTalkClient.sendTextMessage(new TextMessage(form.getText(), messageAt));
            return;
        }

        String title = form.getTitle() == null || form.getTitle().isBlank() ? form.getText() : form.getTitle();
        if (translatedText != null && !translatedText.isBlank()) {
            dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                    title,
                    form.getText() + "\n"
                            + "##### 译文\n"
                            + translatedText,
                    messageAt));
        } else {
            dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                    title,
                    form.getText(),
                    messageAt));
        }
    }
}
