package com.cfz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfz.common.dto.SysMenuDto;
import com.cfz.entity.SysMenu;
import com.cfz.entity.SysUser;
import com.cfz.mapper.SysMenuMapper;
import com.cfz.mapper.SysUserMapper;
import com.cfz.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfz.service.SysUserService;
import com.cfz.utils.MenuTreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2022-07-02
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    MenuTreeUtil menuTreeUtil;

    /**
     * 获取用户菜单和权限信息
     *
     * @return
     */
    @Override
    public List<SysMenuDto> getCurrentUserNav() {

        //获取用户名
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //获取用户信息
        SysUser sysUser = sysUserService.getByUserName(username);

        //获取该用户的菜单信息
        List<Long> menuIds = sysUserMapper.getNavMenuId(sysUser.getId());
        List<SysMenu> menus = this.listByIds(menuIds);

        //转为树状结构
        List<SysMenu> menuTree = menuTreeUtil.buildTree(menus);

        //转化
        return convert(menuTree);
    }

    /**
     * 获取所有用户菜单（树状结构）
     * @return
     */
    @Override
    public List<SysMenu> tree() {

        //获取所有用户菜单
        List<SysMenu> menuList = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));

        //转为树状结构
        return menuTreeUtil.buildTree(menuList);
    }

    /**
     * sysMenu转化为对应的DTO
     * @param menuTree
     * @return
     */
    private List<SysMenuDto> convert(List<SysMenu> menuTree) {

        List<SysMenuDto> menuDtos = new ArrayList<>();

        menuTree.forEach(menu -> {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(menu.getId());
            dto.setName(menu.getPerms());
            dto.setTitle(menu.getName());
            dto.setComponent(menu.getComponent());
            dto.setPath(menu.getPath());

            if (menu.getChildren().size() > 0) {

                dto.setChildren(convert(menu.getChildren()));

            }

            menuDtos.add(dto);

        });


        return menuDtos;
    }


}
