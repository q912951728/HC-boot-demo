package com.ztj.hcboot.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public Producer captchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no"); // 无边框
        properties.setProperty("kaptcha.textproducer.font.color", "black"); // 字体颜色
        properties.setProperty("kaptcha.textproducer.char.space", "5"); // 文字间距
        properties.setProperty("kaptcha.textproducer.char.length", "5"); // 验证码长度
        properties.setProperty("kaptcha.image.width", "120"); // 图片宽度
        properties.setProperty("kaptcha.image.height", "40"); // 图片高度
        properties.setProperty("kaptcha.textproducer.font.size", "30"); // 字体大小

        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
