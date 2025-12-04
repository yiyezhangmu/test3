package com.coolcollege.intelligent.facade.dto.role;

import lombok.Data;


/**
 * @author zhangchenbiao
 * @FileName: RoleDTO
 * @Description:
 * @date 2022-11-07 14:41
 */
@Data
public class RoleDTO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否预制
     */
    @Deprecated
    private Integer isInternal;

    private String appMenu;

    /**
     * 岗位来源:(create:自建岗位, sync:从钉钉同步的角色, sync_position:钉钉同步的职位)
     */
    private String source;

    private String roleAuth;

    private String positionType;

    /**
     * 钉钉角色id
     */
    private Long synDingRoleId;
    /**
     * 角色排序
     */
    private Integer priority;

    /**
     * 角色枚举用于判定逻辑
     */
    private String roleEnum;

}
