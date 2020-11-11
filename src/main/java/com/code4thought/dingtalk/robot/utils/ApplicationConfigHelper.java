package com.code4thought.dingtalk.robot.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/5/16
 */
@Component
public class ApplicationConfigHelper {

    private static ApplicationConfigHelper INSTANCE;

    private final Environment environment;

    public ApplicationConfigHelper(Environment environment) {
        this.environment = environment;
        INSTANCE = this;
    }

    /**
     * 从应用程序配置中获取配置项
     *
     * @param key 配置项键
     * @return
     */
    public static String getConfig(String key) {
        return INSTANCE.environment.getProperty(key);
    }

    /**
     * 从应用程序配置中获取配置项
     *
     * @param key          配置项键
     * @param defaultValue 如果没有该项，返回默认值
     * @return
     */
    public static String getConfig(String key, String defaultValue) {
        return INSTANCE.environment.getProperty(key, defaultValue);
    }
}
