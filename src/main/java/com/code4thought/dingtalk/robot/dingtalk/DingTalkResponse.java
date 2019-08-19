package com.code4thought.dingtalk.robot.dingtalk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DingTalkResponse {

    private String errcode;
    private String errmsg;

    public DingTalkResponse validate() {
        if (errcode != null && !errcode.equals("0")) {
            throw new RuntimeException("DingTalk Api Exception, errorCode: " + errcode + ", errorMessage: " + errmsg);
        }

        return this;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
