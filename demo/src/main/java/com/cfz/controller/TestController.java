package com.cfz.controller;

import com.cfz.common.lang.Result;
import com.cfz.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController extends BaseController{


    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public  Result test() {
        return Result.succ(sysUserService.list());
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/test/pass")
    public  Result pass() {

        //用户输入的密码
        String password = bCryptPasswordEncoder.encode("111111");

        //匹配密码
        boolean matches = bCryptPasswordEncoder.matches("111111", password);
        System.out.println(matches);

        return Result.succ(password);
    }

}
