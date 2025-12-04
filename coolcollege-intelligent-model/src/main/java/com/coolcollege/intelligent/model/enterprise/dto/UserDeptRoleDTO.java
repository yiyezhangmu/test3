package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.enterprise.DeptIdDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/10/26 11:18
 */
@Data
public class UserDeptRoleDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String name;

    private String position;

    /**
     * 是否是部门主管
     * 字段格式{"1": true, "1254243": false}
     */
    private String isLeaderInDept;

    /**
     * 部门id列表
     */
    private List<DeptIdDTO> deptList;

    private List<String> deptIds;

    /**
     * 角色id列表
     */
    private List<SysRoleDO> roles;

    private List<Long> roleIds;
}
