package com.coolcollege.intelligent.model.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/22
 */
@Data
public class MenuTreeVO {
    private String path;
    private String code;
    private String name;
    private Long id;
    private Long parentId;
    private String target;
    private String component;
    private String icon;
    private String type;
    /**
     * 排序
     */
    private Integer sort;
    private String platform;
    /**
     * 常用功能图标
     */
    private String commonFunctionsIcon;
    private Integer menuType;
    private List<String> authorityList;
    private List<MenuTreeVO> children;
    private String env;

    private String defineName;
    private String menuPic;

    private String label;
}
