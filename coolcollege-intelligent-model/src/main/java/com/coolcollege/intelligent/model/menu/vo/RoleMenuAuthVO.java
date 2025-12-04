package com.coolcollege.intelligent.model.menu.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/22
 */
@Data
public class RoleMenuAuthVO {
    private Long id;
    private String checked;
    private String code;
    private String name;
    private String defineName;
    private Long parentId;
    private String path;
    private List<RoleMenuAuthVO> children;
    private List<RoleMenuAuthVO> authorityList;

}
