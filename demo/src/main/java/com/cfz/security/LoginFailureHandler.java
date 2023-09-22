package com.cfz.security;

import cn.hutool.json.JSONUtil;
import com.cfz.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*登陆失败*/
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        Result fail = Result.fail(e.getMessage().equals("Bad credentials") ? "用户名或密码错误" : e.getMessage());

        outputStream.write(JSONUtil.toJsonStr(fail).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

}
