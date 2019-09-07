package com.code4thought.dingtalk.robot.api;

import com.code4thought.dingtalk.robot.api.form.TranslateForm;
import com.code4thought.dingtalk.robot.dingtalk.DingTalkClient;
import com.code4thought.dingtalk.robot.dingtalk.MarkdownMessage;
import com.code4thought.dingtalk.robot.dingtalk.MessageAt;
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

    @PostMapping("/translate/{token}")
    public void translateAndSend(@PathVariable String token, @RequestBody TranslateForm form) {

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
}
