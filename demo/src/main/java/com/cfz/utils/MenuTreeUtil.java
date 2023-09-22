package com.cfz.utils;

import com.cfz.entity.SysMenu;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * List转为树状结构工具类(递归)
 */
@Component
public class MenuTreeUtil {


    //生成menuTree
    public List<SysMenu> buildTree(List<SysMenu> menus) {

        List<SysMenu> tree = new ArrayList<>();

        for (SysMenu menu : menus) {

            if (menu.getParentId().equals(0L)) {
                tree.add(findChildren(menu,menus));
            }

        }

        return tree;

    }

    //查找子节点
    public SysMenu findChildren(SysMenu menu, List<SysMenu> menus) {

        for (SysMenu other : menus) {
            if (other.getParentId().equals(menu.getId())) {
                menu.getChildren().add(findChildren(other,menus));
            }
        }

        return menu;
    }

}
