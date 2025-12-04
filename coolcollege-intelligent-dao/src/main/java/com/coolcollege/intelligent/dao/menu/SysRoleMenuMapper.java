package com.coolcollege.intelligent.dao.menu;

import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Mapper
public interface SysRoleMenuMapper {


    void batchInsertSysRoleMenu(@Param("eid") String enterpriseId,
                                @Param("roleMenuList") List<SysRoleMenuDO> sysRoleMenuDOList);

    List<SysRoleMenuDO> listSysRoleMenuByRoleIdOld(@Param("eid") String enterpriseId,
                                                @Param("roleId")Long roleId,
                                                @Param("platform")String platform);
    List<SysRoleMenuDO> listSysRoleMenuByRoleId(@Param("eid") String enterpriseId,
                                                @Param("roleId")Long roleId,
                                                @Param("platform")String platform);
    List<SysRoleMenuDO> listSysRoleMenuAll(@Param("eid") String enterpriseId);

    List<SysRoleMenuDO> listSysRoleMenuByPlatform(@Param("eid") String enterpriseId,
                                     @Param("platform")String platform);

    void batchInsertRoleMenu(@Param("eid") String enterpriseId,
                             @Param("roleMenuList") List<SysRoleMenuDO> sysRoleMenuDOList);

    List<Long> listSysRoleMenuIdByMenuId(@Param("eid") String enterpriseId,
                                         @Param("platform")String platform,
                                         @Param("menuId")Long menuId);

    List<SysRoleMenuDO> listRoleMenuIdByMenuIds(@Param("eid") String enterpriseId,
                                         @Param("menuIds")List<Long> menuIds);

    /**
     * 删除权限
     * @param enterpriseId
     * @param ids
     */
    void deleteAuthByMenuIds(@Param("eid") String enterpriseId, @Param("ids")List<Long> ids);
}
