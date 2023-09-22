package com.cfz.security;

import cn.hutool.json.JSONUtil;
import com.cfz.common.lang.Result;
import com.cfz.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*登陆成功*/
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        /*生成jwt，并放置请求头中*/
        String jwt = jwtUtil.generateToken(authentication.getName());
        httpServletResponse.setHeader(jwtUtil.getHeader(),jwt);

        Result succ = Result.succ("");

        outputStream.write(JSONUtil.toJsonStr(succ).getBytes("UTF-8"));

        outputStream.flush();
        outputStream.close();

    }
}
