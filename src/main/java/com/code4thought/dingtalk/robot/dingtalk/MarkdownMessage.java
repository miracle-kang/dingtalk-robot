package com.code4thought.dingtalk.robot.dingtalk;

public class MarkdownMessage implements IDingTalkMessage {

    private MarkdownMessageContent markdown;
    private MessageAt at;

    public MarkdownMessage(String title, String content, MessageAt at) {
        this.markdown = new MarkdownMessageContent(title, content);
        this.at = at;
    }

    @Override
    public String getMsgType() {
        return "markdown";
    }

    public MarkdownMessageContent getMarkdown() {
        return markdown;
    }

    public MessageAt getAt() {
        return at;
    }
}
