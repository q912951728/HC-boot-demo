package com.ztj.hcboot.controller;

import com.google.code.kaptcha.Producer;
import com.ztj.hcboot.vo.RespBean;
import com.ztj.hcboot.vo.RespBeanEnum;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class CaptchaController {
    private final Producer captchaProducer;
    private final StringRedisTemplate redisTemplate;

    // 这里使用 @Autowired 自动注入
    public CaptchaController(Producer captchaProducer, StringRedisTemplate redisTemplate) {
        this.captchaProducer = captchaProducer;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/captcha")
    public void getCaptcha(@RequestParam String captchaKey, HttpServletResponse response) throws IOException {
        // 生成验证码文本
        String captchaText = captchaProducer.createText();

        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);

        // 存入 Redis，有效期 5 分钟
        redisTemplate.opsForValue().set("captcha:" + captchaKey, captchaText, 2, TimeUnit.MINUTES);

        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store, no-cache");

        // 输出验证码图片
        ImageIO.write(image, "png", response.getOutputStream());
    }



    @PostMapping("/verify")
    public RespBean verifyCaptcha(@RequestBody Map<String, String> body) {
        String captchaKey = body.get("captchaKey");
        String captchaCode = body.get("captchaCode");


        // 获取 Redis 存储的验证码
        String storedCaptcha = redisTemplate.opsForValue().get("captcha:" + captchaKey);

        if (storedCaptcha == null) {
            return RespBean.error(RespBeanEnum.CAPTCHA_EXPIRED);
        }
        if (!storedCaptcha.equalsIgnoreCase(captchaCode)) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        // 验证通过，删除 Redis 中的验证码
        redisTemplate.delete("captcha:" + captchaKey);
        return RespBean.success("验证码校验成功");
    }

}
