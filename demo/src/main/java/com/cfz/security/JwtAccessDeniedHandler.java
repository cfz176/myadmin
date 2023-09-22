package com.cfz.security;

import cn.hutool.json.JSONUtil;
import com.cfz.common.lang.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//用来解决认证过的用户访问无权限资源时的异常
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        //设置状态码
        httpServletResponse.setStatus(httpServletResponse.SC_FORBIDDEN);
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        Result fail = Result.fail(e.getMessage());

        outputStream.write(JSONUtil.toJsonStr(fail).getBytes("UTF-8"));

        outputStream.flush();
        outputStream.close();

    }
}
