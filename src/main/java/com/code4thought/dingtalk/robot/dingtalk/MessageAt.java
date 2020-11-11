package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageAt {

    private List<String> atMobiles;

    @JsonProperty("isAtAll")
    private Boolean atAll;

    private MessageAt(List<String> atMobiles, Boolean atAll) {
        this.atMobiles = atMobiles;
        this.atAll = atAll;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public Boolean getAtAll() {
        return atAll;
    }

    public static MessageAt noAt() {
        return new MessageAt(null, false);
    }

    public static MessageAt atAll() {
        return new MessageAt(null, true);
    }

    public static MessageAt at(String... atMobiles) {
        return new MessageAt(Arrays.asList(atMobiles), false);
    }
}
