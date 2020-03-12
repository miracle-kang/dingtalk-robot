package com.code4thought.dingtalk.robot.translate;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2020/3/12
 */
public interface TranslateApi {

    String translate(String text);

    String translate(String text, String from, String to);
}
