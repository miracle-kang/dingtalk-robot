package com.code4thought.dingtalk.robot.api.form;

import javax.validation.constraints.NotBlank;

public class MessageForm {

    private String title;

    @NotBlank(message = "Text must not be null.")
    private String text;

    private Boolean translate;
    private Boolean atAll;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Boolean getTranslate() {
        return translate;
    }

    public Boolean getAtAll() {
        return atAll;
    }

    @Override
    public String toString() {
        return "MessageForm{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", translate=" + translate +
                ", atAll=" + atAll +
                '}';
    }
}
