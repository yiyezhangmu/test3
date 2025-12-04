package com.coolcollege.intelligent.model.system.VO;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/11/17
 */
@Data
public class SysRoleBaseVO {

    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 岗位来源:(create:自建岗位, sync:从钉钉同步的角色, sync_position:钉钉同步的职位)
     */
    private String source;

    private String positionType;

}
