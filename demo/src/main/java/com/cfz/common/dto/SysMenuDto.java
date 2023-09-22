package com.cfz.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *             name: 'SysManga',
 *             title: '系统管理',
 *             icon: 'el-icon-s-operation',
 *             pathe: '',
 *             component: '',
 *             children:
 */

@Data
public class SysMenuDto implements Serializable {

    private Long id;
    private String name;
    private String title;
    private String icon;
    private String path;
    private String component;
    private List<SysMenuDto> children = new ArrayList<>();

}
