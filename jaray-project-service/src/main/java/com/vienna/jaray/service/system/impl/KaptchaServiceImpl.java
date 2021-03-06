package com.vienna.jaray.service.system.impl;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.vienna.jaray.service.system.KaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

//import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Jaray
 * @date 2020年09月12日 13:58
 * @description: 验证码服务实现类
 */
@Slf4j
@Service
public class KaptchaServiceImpl implements KaptchaService {
    @Autowired
    private Producer captchaProducer = null;
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
    public void kaptchaImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try(ServletOutputStream out = response.getOutputStream();) {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            //生成验证码
            String capText = captchaProducer.createText();
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

            //向客户端写出
            BufferedImage bi = captchaProducer.createImage(capText);
            ImageIO.write(bi, "jpg", out);
        } catch(IOException e) {
            log.error("IO异常", e);
        }
    }
}
