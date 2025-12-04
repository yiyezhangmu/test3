package com.coolcollege.intelligent.model.menu;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Data
public class SysRoleMenuDO {
    private Long id;
    private Long menuId;
    private Long roleId;
    private String platform;

    public SysRoleMenuDO() {
    }

    public SysRoleMenuDO(Long menuId, Long roleId, String platform) {
        this.menuId = menuId;
        this.roleId = roleId;
        this.platform = platform;
    }
}
