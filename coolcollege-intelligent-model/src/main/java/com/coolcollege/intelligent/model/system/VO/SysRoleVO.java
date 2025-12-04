package com.coolcollege.intelligent.model.system.VO;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.menu.SysMenuDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.List;

@Data
public class SysRoleVO extends SysRoleDO {
    /**
     * 角色相关的人数
     */
    private Long  personNums;
    /**
     * 用户
     */
    private List<EnterpriseUserDO> enterpriseDOs;
    /**
     * 角色下拥有的权限
     */
    private List<SysMenuDO>  sysMenuDOs;
    /**
     * 角色是否可删除
     */
    private Boolean delete=true;

}
