package com.cfz.service;

import com.cfz.common.dto.SysMenuDto;
import com.cfz.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2022-07-02
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取当前用户菜单
     * @return
     */
    List<SysMenuDto> getCurrentUserNav();

    /**
     * 获取用户菜单并转为树状结构
     * @return
     */
    List<SysMenu> tree();

}
