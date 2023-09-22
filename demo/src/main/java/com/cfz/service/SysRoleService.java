package com.cfz.service;

import com.cfz.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 我的公众号：MarkerHub
 * @since 2022-07-02
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 删除校色
     * @param roleIds
     * @return
     */
    void remove(Long[] roleIds);

    /**
     * 权限修改
     * @param roleId
     * @param menuIds
     */
    void perm(Long roleId, Long[] menuIds);

    /**
     * 根据用户Id查询角色
     * @return
     */
    List<SysRole> listRoleByUserId(Long userId);
}
