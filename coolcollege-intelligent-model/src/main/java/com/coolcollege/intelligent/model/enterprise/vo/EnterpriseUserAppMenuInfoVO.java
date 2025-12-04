package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class EnterpriseUserAppMenuInfoVO {

    private Long menuId;
    private String menuName;
    private String menuKey;
    private Integer sort;
    private Long parentId;
    private String type;
    private Integer menuType;
    private List<EnterpriseUserAppMenuInfoVO> children;
    private String icon;

    private String platform;
    private String defineName;
    private String menuPic;
    private String path;
}
