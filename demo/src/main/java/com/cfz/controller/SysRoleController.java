package com.cfz.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfz.common.lang.Const;
import com.cfz.common.lang.Result;
import com.cfz.entity.SysMenu;
import com.cfz.entity.SysRole;
import com.cfz.entity.SysRoleMenu;
import com.cfz.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @since 2022-07-02
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    /**
     * 查找角色及菜单信息
     *
     * @param roleId
     * @return
     */
    @GetMapping("/info/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable("roleId") Long roleId) {

        //获取角色信息
        SysRole sysRole = sysRoleService.getById(roleId);

        //获取该角色的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        List<Long> menuIds = roleMenus.stream().map(menus -> menus.getMenuId()).collect(Collectors.toList());

        sysRole.setMenuIds(menuIds);

        return sysRole != null ? Result.succ(sysRole) : Result.fail("error");
    }

    /**
     * 查早全部角色信息
     *
     * @param name
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name) {

        Page<SysRole> pageData = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>().like(StrUtil.isNotBlank(name), "name", name));

        return Result.succ(pageData);
    }

    /**
     * 新增角色
     *
     * @param sysRole
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@Validated @RequestBody SysRole sysRole) {

        //添加时间和状态
        sysRole.setCreated(LocalDateTime.now());

        //新增
        boolean save = sysRoleService.save(sysRole);

        return save ? Result.succ("success") : Result.fail("error");
    }

    /**
     * 血钙角色信息
     *
     * @param sysRole
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole) {

        sysRole.setUpdated(LocalDateTime.now());

        sysRoleService.updateById(sysRole);

        //更新缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());

        return Result.succ(sysRole);
    }

    /**
     * 删除角色（含批量删除）
     *
     * @param roleIds
     * @return
     */
    @PostMapping("/delete")
    @Transactional
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result delete(@RequestBody Long[] roleIds) {
        //删除角色
        sysRoleService.remove(roleIds);
        return Result.succ("");
    }

    /**
     * 修改权限
     * @param roleId
     * @param menuIds
     * @return
     */
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result perm(@PathVariable(name = "roleId") Long roleId, @RequestBody Long[] menuIds) {
        sysRoleService.perm(roleId, menuIds);
        return Result.succ("");
    }


}
