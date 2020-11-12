package com.code4thought.dingtalk.robot.api;

import com.code4thought.dingtalk.robot.api.form.MessageForm;
import com.code4thought.dingtalk.robot.api.form.TweetForm;
import com.code4thought.dingtalk.robot.dingtalk.DingTalkClient;
import com.code4thought.dingtalk.robot.dingtalk.MarkdownMessage;
import com.code4thought.dingtalk.robot.dingtalk.MessageAt;
import com.code4thought.dingtalk.robot.dingtalk.TextMessage;
import com.code4thought.dingtalk.robot.translate.TranslateApi;
import com.code4thought.dingtalk.robot.utils.ApplicationConfigHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    private final TranslateApi translateApi;

    public WebResources(TranslateApi translateApi) {
        this.translateApi = translateApi;
    }

    @PostMapping("/dingTalk/{token}/tweetMessage")
    public void newTweet(@PathVariable String token, @RequestBody String strParam) {

        String[] params = strParam.split(";;;");
        if (params.length != 4) {
            logger.error("Unknown params: {}", strParam);
            return;
        }
        TweetForm form = new TweetForm(params[0], params[1], params[2], params[3]);
        logger.info("{} new tweeted: {}", form.getUsername(), form);

        String translatedText = translateApi.translate(form.getText());
        logger.info("Translated Text: {}", translatedText);

        DingTalkClient dingTalkClient = new DingTalkClient(token, getSecret(form.getUsername()));
        dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                "@" + form.getUsername() + " tweeted",
                "#### " + form.getText() + "\n"
                        + "##### 译文\n"
                        + "#### " + (StringUtils.isEmpty(translatedText) ? "翻译失败" : translatedText) + "\n"
                        + "##### [原文链接](" + form.getLinkToTweet() + ")\n"
                        + "###### " + form.getCreatedAt(),
                null));

        logger.info("Send ding talk message result OK.");
    }

    @PostMapping("/dingTalk/{token}/message")
    public void newMessage(@PathVariable String token, @RequestBody MessageForm form) {

        logger.info("New message {}", form);

        String translatedText = null;
        if (form.getTranslate() != null && form.getTranslate()) {
            translatedText = translateApi.translate(form.getText());
            logger.info("Translated Text: {}", translatedText);
        }

        DingTalkClient dingTalkClient = new DingTalkClient(token, getSecret(form.getAccount()));
        MessageAt messageAt = form.getAtAll() != null && form.getAtAll() ? MessageAt.atAll() : null;
        if (translatedText == null && (form.getTitle() == null || form.getTitle().isBlank())) {
            dingTalkClient.sendTextMessage(new TextMessage(form.getText(), messageAt));
            return;
        }

        String title = form.getTitle() == null || form.getTitle().isBlank() ? form.getText() : form.getTitle();
        if (translatedText != null && !translatedText.isBlank()) {
            dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                    title,
                    "#### " + form.getText() + "\n"
                            + "##### 译文\n"
                            + "#### " + translatedText,
                    messageAt));
        } else {
            dingTalkClient.sendMarkdownMessage(new MarkdownMessage(
                    title,
                    form.getText(),
                    messageAt));
        }
    }

    private String getSecret(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String secret = ApplicationConfigHelper.getConfig("dingtalk.robot.secret." + username);
        if (secret == null || secret.isBlank()) {
            return null;
        }
        return secret;
    }
}
