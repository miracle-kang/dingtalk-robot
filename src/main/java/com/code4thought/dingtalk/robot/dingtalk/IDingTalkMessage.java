package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/15
 */
public interface IDingTalkMessage {

    @JsonProperty("msgtype")
    String getMsgType();
}
