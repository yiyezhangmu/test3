package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * 企业用户角色
 *
 * @ClassName: EntUserRoleDTO
 * @Author: xugangkun
 * @Date: 2021/3/31 16:04
 */
@Data
public class EntUserRoleDTO {
    /**
     * 用户角色id
     */
    private Long userRoleId;
    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;

    private String roleEnum;

    /**
     * 岗位来源:(create:自建岗位, sync:从钉钉同步的角色, sync_position:钉钉同步的职位)
     */
    private String source;


    private String userId;

    /**
     * 权限同步类型 1:同步过来的
     */
    private Integer syncType;


}
