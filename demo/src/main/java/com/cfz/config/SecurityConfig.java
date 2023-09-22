package com.cfz.config;

import com.cfz.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity /*加载Security安全相关配置*/
@EnableGlobalMethodSecurity(prePostEnabled = true) /*@EnableGlobalMethodSecurity:哪些方法是需要权限的。(prePostEnabled = true：在post请求之前都要进行权限校验*/
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    UserDetailServiceImp userDetailServiceImp;

    @Autowired
    JwtLoginOutSuccessHandler jwtLoginOutSuccessHandler;

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }

    //密码加密方式
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    };


    //拦截白名单
    private static final String[] URL_WHITELIST = {

            "/login",
            "/loginout",
            "/captcha",
            "/favicon.ico",

    };

    protected void configure(HttpSecurity http) throws Exception {

        //关闭预防攻击
        http.cors().and().csrf().disable()

                //登陆配置
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)

                //退出配置
                .and()
                .logout()
                .logoutSuccessHandler(jwtLoginOutSuccessHandler)

                //禁用sessino
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //拦截规则
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()
                //异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)/* 用来解决匿名用户访问无权限资源时的异常*/
                .accessDeniedHandler(jwtAccessDeniedHandler)/* 用来解决认证过的用户访问无权限资源时的异常*/

                //配置自定义的过滤器
                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);/*添加到用户名，密码验证之前*/

    }

    //托管密码验证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailServiceImp);
    }
}
