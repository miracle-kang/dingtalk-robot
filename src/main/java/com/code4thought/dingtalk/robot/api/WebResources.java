package com.code4thought.dingtalk.robot.api;

import com.code4thought.dingtalk.robot.api.form.TranslateForm;
import org.springframework.web.bind.annotation.*;

/**
 * Specified here
 *
 * @author kangliqi
 * @date 2019/8/31
 */
@RestController
@RequestMapping("/")
public class WebResources {

    @PostMapping("/translate/{token}")
    public void translateAndSend(@PathVariable String token, @RequestBody TranslateForm form) {

    }
}
