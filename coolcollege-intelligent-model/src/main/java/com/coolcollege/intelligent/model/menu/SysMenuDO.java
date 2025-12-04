package com.coolcollege.intelligent.model.menu;


import lombok.Data;

/**
 * 菜单表
 *
 * @author shoul
 */
@Data
public class SysMenuDO {

    /**
     * ID
     */
    private Long id;
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 菜单编号
     */
    private String code;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 菜单别名
     */
    private String alias;
    /**
     * 请求地址(前端路由)
     */
    private String path;
    /**
     * 后端权限标识
     */
    private String perms;
    /**
     * 菜单资源(图片)
     */
    private String source;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 菜单类型(菜单，按钮)
     */
    private Integer category;
    /**
     * 操作按钮类型(工具栏，操作栏，工具操作栏)
     */
    private Integer action;
    /**
     * 描述
     */
    private String remark;
    /**
     * 所属项目（PC，小程序）
     */
    private String platform;
    /**
     * 是否已删除
     */
    private Integer isDeleted;
    /**
     * 操作类型
     */
    private String type;
    /**
     * 是否新开页面
     */
    private String target;
    /**
     * 组件
     */
    private String component;
    /**
     * 图标
     */
    private String icon;
    /**
     * 是否选中
     */
    private Boolean isChecked;

    private Integer menuType;

    /**
     * 菜单环境
     */
    private String env;

    /**
     * 常用功能图标
     */
    private String commonFunctionsIcon;

    private String label;
}
