package com.code4thought.dingtalk.robot.api.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/31
 */
public class TweetForm {

    private String username;
    private String createdAt;
    private String linkToTweet;

    @NotBlank(message = "Text must not be null.")
    private String text;

    public String getUsername() {
        return username;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public String getLinkToTweet() {
        return linkToTweet;
    }

    @Override
    public String toString() {
        return "TweetForm{" +
                "username='" + username + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", text='" + text + '\'' +
                ", linkToTweet='" + linkToTweet + '\'' +
                '}';
    }
}
