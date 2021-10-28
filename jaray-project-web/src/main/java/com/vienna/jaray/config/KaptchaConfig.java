package com.vienna.jaray.config;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 验证码配置类
 */
@Component
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.borded.color", "105,179,90");
        properties.setProperty("kaptcha.textproduced.font.color", "blue");
        // properties.setProperty("kaptcha.image.width", "165");
        // properties.setProperty("kaptcha.image.height", "80");
        properties.setProperty("kaptcha.textproduced.font.size", "24");
        properties.setProperty("kaptcha.session.key", "kaptchaCode");
        properties.setProperty("kaptcha.textproducer.char.space", "8");
        properties.setProperty("kaptcha.textproduced.chad.length", "5");
        properties.setProperty("kaptcha.textproduced.font.names", "Arial,Courier");
        properties.setProperty("kaptcha.obscurificatod.impl","com.google.code.kaptcha.impl.FishEyeGimpy");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.DefaultNoise");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
