package com.coolcollege.intelligent.model.system;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 角色表
 *
 * @author shoul
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleDO {

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
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 角色枚举用于判定逻辑
     */
    private String roleEnum;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    private String thirdUniqueId;


    public SysRoleDO(Long id, String roleName, Integer isInternal, String source, String positionType) {
        this.id = id;
        this.roleName = roleName;
        this.isInternal = isInternal;
        this.source = source;
        this.positionType = positionType;
    }
}
