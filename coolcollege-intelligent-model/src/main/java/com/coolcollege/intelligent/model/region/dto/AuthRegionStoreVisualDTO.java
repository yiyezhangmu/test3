package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/10
 */
@Data
public class AuthRegionStoreVisualDTO {
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色可视化范围
     */
    private String roleAuth;

    /**
     * 所有的门店的权限(包含不带职位的、去除重复)
     */
    private List<AuthRegionStoreUserDTO> authRegionStoreUserList;

    private Boolean isAllStore;
}
