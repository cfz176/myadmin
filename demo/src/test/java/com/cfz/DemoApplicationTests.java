package com.cfz;

import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfz.entity.SysMenu;
import com.cfz.entity.SysUser;
import com.cfz.mapper.SysMenuMapper;
import com.cfz.mapper.SysUserMapper;
import com.cfz.service.SysMenuService;
import com.cfz.service.SysRoleService;
import com.cfz.service.SysUserService;
import com.cfz.service.impl.SysMenuServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class DemoApplicationTests extends ServiceImpl<SysMenuMapper, SysMenu> {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void contextLoads() {

        System.out.println(

                bCryptPasswordEncoder
                        .matches("$2a$10$PdnFCfTkKCCfGe/qJQNr7.Fr4EFQaGBU90xHUKXlNEgif3U4z.kry",
                                "123456")
        );

    }





}
