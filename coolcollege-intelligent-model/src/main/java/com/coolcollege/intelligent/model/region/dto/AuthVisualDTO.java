package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe: 权限可视化DTO
 *
 * @author zhouyiping
 * @date 2020/10/14
 */
@Data
public class AuthVisualDTO {
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
    private List<String> storeIdList;

    private Boolean isAllStore;




}
