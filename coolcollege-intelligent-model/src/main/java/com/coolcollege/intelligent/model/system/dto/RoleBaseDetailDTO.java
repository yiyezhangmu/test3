package com.coolcollege.intelligent.model.system.dto;

import com.coolcollege.intelligent.model.menu.vo.RoleMenuAuthVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/08
 */
@Data
public class RoleBaseDetailDTO extends SysRoleDO {

    /**
     * 菜单权限
     */
    List<RoleMenuAuthVO> roleMenuAuthList;
    /**
     * 角色Id
     */
    private Long roleId;
    private List<AppMenuDTO> appMenuList;



}
