package com.cfz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfz.service.*;
import com.cfz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    /**
     *
     * @return
     */
    public Page getPage() {
//        int current = ServletRequestUtils.getIntParameter(req, "current", 1);
//        int size = ServletRequestUtils.getIntParameter(req, "size", 10);

        int current = ServletRequestUtils.getIntParameter(req, "current", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 10);


        return new Page(current,size);
    }

}
