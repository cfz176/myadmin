package com.cfz.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cfz.common.exception.CaptchaException;
import com.cfz.common.lang.Const;
import com.cfz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String url = httpServletRequest.getRequestURI();

        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {

            try {

                //校验验证
                //如果验证码不正确，跳转到认证失败处理器
                validate(httpServletRequest);

            } catch (CaptchaException e) {

                //交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);

            }
        }

        //认证成功
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    //验证码校验
    private void validate(HttpServletRequest httpServletRequest) {

        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("key");

        //判断验证码是否为空
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }

        //判断验证码是否正确
        if (!code.equals(redisUtil.hget(Const.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码错误");
        }

        //验证码一次使用
        redisUtil.hdel(Const.CAPTCHA_KEY, key);

    }
}
