package com.cfz.security;

import cn.hutool.core.util.StrUtil;
import com.cfz.entity.SysUser;
import com.cfz.service.SysUserService;
import com.cfz.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//验证用户的jwt
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Autowired
    UserDetailServiceImp userDetailServiceImp;

    @Autowired
    SysUserService sysUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //获取jwt
        String jwt = request.getHeader(jwtUtil.getHeader());
        //判断jwt是否为空
        if (StrUtil.isBlankOrUndefined(jwt)) {
            chain.doFilter(request, response);/*jwt为空，则放行，交给后面的过滤器判断是否为白名单*/
            return;
        }

        //解析jwt
        Claims claimsByToken = jwtUtil.getClaimsByToken(jwt);
        //判读jwt是否为空
        if (claimsByToken == null) {
            throw new JwtException("token 异常");
        }
        //判断jwt是否过期
        if (jwtUtil.isTokenExpired(claimsByToken)) {
            throw new JwtException("token 已过期");
        }

        //获取用户权限信息等
        String username = claimsByToken.getSubject();

        SysUser sysUser = sysUserService.getByUserName(username);

        //组成UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, sysUser.getPassword(), userDetailServiceImp.getUserAuthority(sysUser.getId()));

        //设置认证主体
        SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(request, response);
    }
}
