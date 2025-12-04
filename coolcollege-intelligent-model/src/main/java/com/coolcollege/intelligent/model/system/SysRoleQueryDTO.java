package com.coolcollege.intelligent.model.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleQueryDTO extends SysRoleDO implements Serializable {
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 用户id集合
     */
    private List<String> userIds;
    /**
     * 角色名（用户模糊查询）
     */
    private String role_name;
    /**
     * 分页每页条数
     */
    private Integer pageSize=10;
    /**
     * 分页第几页
     */
    private Integer pageNum=1;
    /**
     * 用户名称
     */
    private String userName;

}
