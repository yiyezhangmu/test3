package com.coolcollege.intelligent.model.system;

import com.coolcollege.intelligent.model.menu.SysMenuDO;
import lombok.Data;

@Data
public class SysMenuMapperDO extends SysMenuDO {
    /**
     * 用户的角色id
     */
    private String roleId;
}
