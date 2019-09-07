package com.code4thought.dingtalk.robot.dingtalk;

public class MarkdownMessageContent {

    private String title;
    private String text;

    public MarkdownMessageContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
