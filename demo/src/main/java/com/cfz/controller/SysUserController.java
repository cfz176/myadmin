package com.cfz.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfz.common.dto.PassDto;
import com.cfz.common.lang.Const;
import com.cfz.common.lang.Result;
import com.cfz.entity.SysRole;
import com.cfz.entity.SysUser;
import com.cfz.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p> *
 *
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    /**
     * 查找该用户信息及角色
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {

        SysUser user = sysUserService.getById(id);
        Assert.notNull(user, "找不到该管理员");

        user.setSysRoles(sysRoleService.listRoleByUserId(id));

        return Result.succ(user);
    }

    /**
     * 查找所有用户信息
     *
     * @param username
     * @return
     */
    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String username) {

        Page<SysUser> pageData = sysUserService.page(getPage(), new QueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username), "username", username));

        pageData.getRecords().forEach(u -> {

            u.setSysRoles(sysRoleService.listRoleByUserId(u.getId()));
        });

        return Result.succ(pageData);
    }


    /**
     * 新增用户
     *
     * @param sysUser
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysUser sysUser) {

        //船舰时间
        sysUser.setCreated(LocalDateTime.now());

        //默认密码
        sysUser.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));

        //默认头像
        sysUser.setAvatar(Const.DEFAULT_AVATAR);

        sysUserService.save(sysUser);

        return Result.succ("success");
    }

    /**
     * 删除用户（批量删除）
     *
     * @param longs
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] longs) {

        //删除
        sysUserService.remove(longs);

        return Result.succ("success");
    }

    /**
     * 修改用户
     *
     * @param sysUser
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysUser sysUser) {

        //修改用户
        sysUserService.update(sysUser);

        return Result.succ("success");
    }

    /**
     * 权限修改
     *
     * @param userId  用户id
     * @param roleIds 权限id列表
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:role')")
    @PostMapping("/role/{userId}")
    public Result role(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds) {

        //权限操作
        sysUserService.changeRole(userId, roleIds);

        return Result.succ("success");
    }

    /**
     * 重置密码
     *
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long userId) {

        SysUser sysuser = sysUserService.getById(userId);

        sysuser.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysuser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysuser);

        return Result.succ("success");
    }

    /**
     * 修改密码
     * @param passDto
     * @param principal
     * @return
     */
    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {

        //查找当前用户
        SysUser user = sysUserService.getByUserName(principal.getName());

        //判断旧密码是否正确
        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), user.getPassword());

        if (!matches) {
            return Result.fail("旧密码不正确");
        }

        user.setPassword(passDto.getPassword());
        user.setUpdated(LocalDateTime.now());

        sysUserService.updateById(user);
        return Result.succ("修改成功");
    }

}
