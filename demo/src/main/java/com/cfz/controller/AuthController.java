package com.cfz.controller;

import cn.hutool.core.map.MapUtil;
import com.cfz.common.lang.Const;
import com.cfz.common.lang.Result;
import com.cfz.entity.SysUser;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.UUID;

@RestController
public class AuthController extends BaseController {

    @Autowired
    Producer producer;

    //验证码
    @GetMapping("/captcha")
    public Result captcha() throws IOException {

        //生成key，code
        String key = UUID.randomUUID().toString();
        String code = producer.createText();

//        测试用
//        key = "aaaaa";
//        code = "11111";

        //生成验证码图片
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //调整输出图片的格式和样式
        ImageIO.write(image, "jpg", outputStream);

        Base64.Encoder encoder = Base64.getEncoder();
        String str = "data:image/jpeg;base64,";

        String base64Image = str + encoder.encodeToString(outputStream.toByteArray());

        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);

        return Result.succ(MapUtil.builder()
                .put("key", key)
                .put("captchaImg", base64Image)
                .build());
    }

    @GetMapping("/sys/userinfo")
    public Result userInfo(Principal principal) {

        SysUser sysUser = sysUserService.getByUserName(principal.getName());

        return Result.succ(MapUtil.builder()
                .put("id", sysUser.getId())
                .put("username", sysUser.getUsername())
                .put("avatar", sysUser.getAvatar())
                .put("created", sysUser.getCreated())
                .map());
    }

}
