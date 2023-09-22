package com.cfz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfz.entity.SysRole;
import com.cfz.entity.SysRoleMenu;
import com.cfz.entity.SysUserRole;
import com.cfz.mapper.SysRoleMapper;
import com.cfz.service.SysRoleMenuService;
import com.cfz.service.SysRoleService;
import com.cfz.service.SysUserRoleService;
import com.cfz.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2022-07-02
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysUserService sysUserService;


    /**
     * 删除角色
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public void remove(Long[] ids) {

        //删除角色
        this.removeByIds(Arrays.asList(ids));

        //删除相关的角色菜单中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", ids));
        //删除相关的用户菜单中间表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", ids));

        //同步删除缓存
        Arrays.stream(ids).forEach(id ->
                sysUserService.clearUserAuthorityInfoByRoleId(id));

    }

    /**
     * 权限膝盖
     * @param roleId
     * @param menuIds
     */
    @Transactional
    @Override
    public void perm(Long roleId, Long[] menuIds) {

        List<SysRoleMenu> roleMenus = new ArrayList<>();

        Arrays.stream(menuIds).forEach(id -> {

            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(id);
            roleMenu.setRoleId(roleId);

            roleMenus.add(roleMenu);

        });

        //先删除就权限，再保存新权限
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(roleMenus);

        //删除缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);

    }

    /**
     * 根据用户Id查询角色
     * @return
     */
    @Override
    public List<SysRole> listRoleByUserId(Long userId) {

        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>()
                .inSql("id", "select role_id from sys_user_role where user_id =" + userId));

        return sysRoles;
    }
}
