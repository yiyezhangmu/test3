package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/10/20 14:03
 */
@Data
public class ManualDeptUserDTO {

    /**
     * 父部门id
     */
    private String pid;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 人员id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 是否是部门主管
     * 字段格式{"1": true, "1254243": false}
     */
    private String isLeaderInDept;

    /**
     * 角色id列表
     */
    private List<SysRoleDO> roles;

    private List<Long> roleIds;
}
