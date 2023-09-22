package com.cfz.service;

import com.cfz.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 我的公众号：MarkerHub
 * @since 2022-07-02
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取用户所有信息
     * @param username
     * @return
     */
    SysUser getByUserName(String username);

    /**
     * 获取用户权限信息
     * @param userId
     * @return
     */
    String getUserAuthorityInfo(Long userId);

    /**
     * 清空用户uthority的Redis缓存
     * @param userName
     */
    void clearUserAuthorityInfo(String userName);


    /**
     * 由角色信息改变，而引发与该角色相关的用户的authority的Redis缓存清空
     * @param roleId
     */
    void clearUserAuthorityInfoByRoleId(Long roleId);

    /**
     * 由于菜单权限改变，而引发与该权限相关的用户的authority的Redis缓存清空
     * @param menuId
     */
    void clearUserAuthorityInfoByMenu(Long menuId);

    /**
     * 批量删除
     * @param longs
     */
    void remove(Long[] longs);

    /**
     * 修改用户
     * @param sysUser
     */
    void update(SysUser sysUser);

    void changeRole(Long userId, Long[] roleIds);
}
