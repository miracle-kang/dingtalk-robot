package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextMessage implements IDingTalkMessage {

    private MessageContent text;
    private MessageAt at;

    public TextMessage(String message) {
        this.text = new MessageContent(message);
    }

    public TextMessage(String message, MessageAt at) {
        this.text = new MessageContent(message);
        this.at = at;
    }

    @Override
    public String getMsgType() {
        return "text";
    }

    public MessageContent getText() {
        return text;
    }

    public MessageAt getAt() {
        return at;
    }
}
