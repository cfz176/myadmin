package com.cfz.security;

import cn.hutool.json.JSONUtil;
import com.cfz.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//用来解决匿名用户未登录时的异常
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");

        //设置状态码
        httpServletResponse.setStatus(httpServletResponse.SC_UNAUTHORIZED);
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        Result fail = Result.fail("请先登录");

        outputStream.write(JSONUtil.toJsonStr(fail).getBytes("UTF-8"));

        outputStream.flush();
        outputStream.close();
    }
}
