package com.cfz.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfz.common.dto.SysMenuDto;
import com.cfz.common.lang.Result;
import com.cfz.entity.SysMenu;
import com.cfz.entity.SysRoleMenu;
import com.cfz.entity.SysUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    /**
     * 获取用户权限及菜单列表
     *
     * @return
     */
    @GetMapping("/nav")
    public Result nav(Principal principal) {

        //获取用户信息
        SysUser sysUser = sysUserService.getByUserName(principal.getName());

        //获取用户权限
        String userAuthorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());
        String[] userAuthorityInfoArray = StringUtils.tokenizeToStringArray(userAuthorityInfo, ",");

        //获取导航栏信息
        List<SysMenuDto> navs = sysMenuService.getCurrentUserNav();

        return Result.succ(MapUtil.builder()
                .put("authority", userAuthorityInfoArray)
                .put("nav", navs)
                .map());
    }

    /**
     * 查询用户信息方法
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable("id") Long id) {
        return Result.succ(sysMenuService.getById(id));
    }

    /**
     * 用户列表全查（树状结构）
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list() {

        //获取所有用户树状列表
        List<SysMenu> menus = sysMenuService.tree();

        return menus != null ? Result.succ(menus) : Result.fail("error");
    }

    /**
     * 新增方法
     * @param sysMenu
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu) {

        //当前时间
        sysMenu.setCreated(LocalDateTime.now());

        //新增
        boolean save = sysMenuService.save(sysMenu);

        return save ? Result.succ("success") : Result.fail("error");
    }

    /**
     * 修改方法
     * @param sysMenu
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody SysMenu sysMenu) {

        //当前时间
        sysMenu.setUpdated(LocalDateTime.now());

        //修改
        boolean update = sysMenuService.updateById(sysMenu);

        //清空所有与该菜单相关的Redis缓存
        sysUserService.clearUserAuthorityInfoByMenu(sysMenu.getId());

        return update ? Result.succ("success") : Result.fail("error");
    }

    /**
     * 删除方法
     * @return
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") long id) {

        //查询子菜单
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        //判断是否存在子菜单
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }

        //删除菜单
        boolean remove = sysMenuService.removeById(id);
        //同步删除中间关联表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));

        //清空所有与该菜单相关的Redis缓存
        sysUserService.clearUserAuthorityInfoByMenu(id);

        return remove?Result.succ("success"):Result.fail("error");
    }

}
