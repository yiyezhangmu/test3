package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.List;

/**
 * describe: 基础的权限DTO 不转换区域和门店
 *
 * @author zhouyiping
 * @date 2021/06/08
 */
@Data
public class AuthBaseVisualDTO {
    /**
     * 用户Id
     */
    private String userId;


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

    private Boolean isAllStore;

    private List<String> storeIdList;

    /**
     * 未处理过的区域Id,直接从配置权表中获取的
     */
    private List<String>  regionIdList;

    /**
     * 获取配置权限的fullRegionPath
     */
    private List<String> fullRegionPathList;


}
