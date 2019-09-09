package com.code4thought.dingtalk.robot.api;

import com.code4thought.dingtalk.robot.api.form.MessageForm;
import com.code4thought.dingtalk.robot.api.form.TweetForm;
import com.code4thought.dingtalk.robot.dingtalk.DingTalkClient;
import com.code4thought.dingtalk.robot.dingtalk.MarkdownMessage;
import com.code4thought.dingtalk.robot.dingtalk.MessageAt;
import com.code4thought.dingtalk.robot.dingtalk.TextMessage;
import com.code4thought.dingtalk.robot.youdao.YouDaoApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final YouDaoApi youDaoApi;

    public WebResources(YouDaoApi youDaoApi) {
        this.youDaoApi = youDaoApi;
    }

    @PostMapping("/dingTalk/{token}/tweetMessage")
    public void newTweet(@PathVariable String token, @RequestBody TweetForm form) {

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

    @PostMapping("/dingTalk/{token}/message")
    public void newMessage(@PathVariable String token, @RequestBody MessageForm form) {

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
