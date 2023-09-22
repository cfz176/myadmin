package com.cfz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfz.entity.SysMenu;
import com.cfz.entity.SysRole;
import com.cfz.entity.SysUser;
import com.cfz.entity.SysUserRole;
import com.cfz.mapper.SysUserMapper;
import com.cfz.service.SysMenuService;
import com.cfz.service.SysRoleService;
import com.cfz.service.SysUserRoleService;
import com.cfz.service.SysUserService;
import com.cfz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2022-07-02
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 获取用户所有信息
     *
     * @param username
     * @return
     */
    @Override
    public SysUser getByUserName(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    /**
     * 获取用户权限信息
     *
     * @param userId
     * @return
     */
    @Override
    public String getUserAuthorityInfo(Long userId) {

        //获取用户信息
        SysUser sysUser = sysUserMapper.selectById(userId);

        //如果Redis里有authority信息，则从Redis中拿到authority
        if (redisUtil.hasKey("GranteAuthorty:" + sysUser.getUsername())) {

            return (String) redisUtil.get("GranteAuthorty:" + sysUser.getUsername());

        } else {

            //格式：ROLE_admin,ROLE_normal,sys:user:list
            String authority = "";

            //获取角色
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>()
                    .inSql("id", "select role_id from sys_user_role where user_id = " + userId));

            //获取roles里的code并用”,“隔开
            if (roles.size() > 0) {
                String roleCodes = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes;
            }

            //获取用户的菜单信息
            List<Long> menuId = sysUserMapper.getNavMenuId(userId);

            //获取menu里的perms用”,“隔开
            if (menuId.size() > 0) {
                List<SysMenu> menus = sysMenuService.listByIds(menuId);
                String menuPerms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
                authority = authority.concat(",").concat(menuPerms);
            }

            //将权限信息存入Redis
            redisUtil.set("GranteAuthorty:" + sysUser.getUsername(), authority, 60 * 60);

            return authority;

        }

    }

    /**
     * 清空用户authority的Redis缓存
     *
     * @param userName
     */
    @Override
    public void clearUserAuthorityInfo(String userName) {
        //清空Redis缓存
        redisUtil.del("GranteAuthorty:" + userName);

    }

    /**
     * 由角色信息改变，而引发与该角色相关的用户的authority的Redis缓存清空
     *
     * @param roleId
     */
    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {

        //获取用户信息
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id", "select user_id from sys_user_role where role_id =" + roleId));

        //清空Redis的authority缓存
        sysUsers.forEach(sysUser -> this.clearUserAuthorityInfo(sysUser.getUsername()));
    }

    /**
     * 由于菜单权限改变，而引发与该权限相关的用户的authority的Redis缓存清空
     *
     * @param menuId
     */
    @Override
    public void clearUserAuthorityInfoByMenu(Long menuId) {

        //获取用户信息
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);

        //清空Redis的authority缓存
        sysUsers.forEach(sysUser -> this.clearUserAuthorityInfo(sysUser.getUsername()));

    }

    /**
     * 删除用户（批量删除）
     * @param longs
     */
    @Transient
    @Override
    public void remove(Long[] longs) {

        //删除用户
        this.removeByIds(Arrays.asList(longs));

        //删除相关的用户角色中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", longs));

    }

    /**
     * 修改用户
     * @param sysUser
     */
    @Transient
    @Override
    public void update(SysUser sysUser) {

        sysUser.setUpdated(LocalDateTime.now());
        //修改用户
        this.updateById(sysUser);

        //清空用户redis缓存
        this.clearUserAuthorityInfo(sysUser.getUsername());
    }

    @Override
    public void changeRole(Long userId, Long[] roleIds) {

        List<SysUserRole> sysUserRoleList = new ArrayList<>();

        Arrays.stream(roleIds).forEach(r -> {

            SysUserRole sysUserRole = new SysUserRole();

            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);

            sysUserRoleList.add(sysUserRole);

        });

        //删除旧权限
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", userId));
        //添加权限
        sysUserRoleService.saveBatch(sysUserRoleList);

        //删除缓存
        SysUser sysUser = this.getById(userId);
        this.clearUserAuthorityInfo(sysUser.getUsername());

    }


}
