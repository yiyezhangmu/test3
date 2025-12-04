package com.coolcollege.intelligent.service.system;

import com.coolcollege.intelligent.common.enums.MenuTypeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.model.menu.SysMenuDO;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.menu.request.AppMenuAddRequest;
import com.coolcollege.intelligent.model.menu.request.AppMenuDeleteRequest;
import com.coolcollege.intelligent.model.menu.request.AppMenuModifyRequest;
import com.coolcollege.intelligent.model.menu.vo.MenuAuthVO;
import com.coolcollege.intelligent.model.menu.vo.MenuTreeVO;
import com.coolcollege.intelligent.model.menu.vo.RoleMenuAuthVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysMenuVO;
import com.coolcollege.intelligent.model.system.dto.AppMenuDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * 菜单表
 *
 * @author 首亮
 */
public interface SysMenuService {

    /**
     * 获得所有菜单
     * @param eid
     * @param parentIds
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:17
     */
    List<SysMenuDO> selectMenuAll(String eid, List<Long> parentIds, String platformType, String env);
    /**
     * 获得所有老菜单
     * @param eid
     * @param parentIds
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:17
     */
    List<SysMenuDO> selectOldMenuAll(String eid, List<Long> parentIds, String platformType, String env);

    /**
     * 通过userId查询user有用菜单权限
     *
     * @param enterpriseId 企业ID
     * @param userId       用户ID
     * @return List<SysMenuDO>
     */
    List<MenuTreeVO> getUserMenus(String enterpriseId, String userId,CurrentUser user,Boolean isOld);

    /**
     * 获取企业的全部菜单
     * @param enterpriseId
     * @return
     */
    List<MenuTreeVO> getEnterpriseMenus(String enterpriseId);

    /**
     * 获取所有菜单树结构
     * @return
     */
    List<MenuTreeVO> getAllMenus();

    /**
     * 菜单下权限列表
     * @param id
     * @return
     */
    List<MenuAuthVO> getMenuAuthList(Long id,String menuPlatFormType);




    /**
     * 查询角色和权限的关系(pc)
     *
     * @param enterpriseId
     * @return
     */
    List<RoleMenuAuthVO> getMenusByRole(String enterpriseId,  SysRoleDO role , PlatFormTypeEnum platFormType);

    /**
     * 根据菜单 和权限 获取vo
     * @param menuList
     * @param authList
     * @return
     */
    List<SysMenuVO> getMenuVO(List<SysMenuDO> menuList, List<SysRoleMenuDO> authList);
    List<RoleMenuAuthVO> getMenusByRole(String enterpriseId,  String  roleId );
    List<AppMenuDTO> getAppMenusByRole(String enterpriseId, SysRoleDO roleDO);
    List<AppMenuDTO> getAppMenusByRoleNew(String enterpriseId, Long roleId);
    List<AppMenuDTO> getAppMenusByUser(String enterpriseId, String userId, CurrentUser currentUser);
    /**
     * 根据用户获取权限
     * @Author chenyupeng
     * @Date 2021/7/9
     * @param enterpriseId
     * @param userId
     * @param currentUser
     * @return: java.util.List<com.coolcollege.intelligent.model.system.dto.AppMenuDTO>
     */
    List<AppMenuDTO> getAppMenusByUserNew(String enterpriseId, String userId, CurrentUser currentUser);


    /**
     * 添加菜单或者是权限
     *
     * @param parentId
     * @param name
     * @param path
     * @param type
     * @param env
     * @return
     */
    Boolean addMenuOrAuth(Long parentId, String name, String path, String type,String target,String component,String icon, MenuTypeEnum menuTypeEnum,
                          String env, String label);

    /**
     * 修改菜单或者权限
     *
     * @param id
     * @param name
     * @param path
     * @param type
     * @param env
     * @return
     */
    Boolean modifyMenuOrAuth(Long id, String name, String path, String type,String target,String component,String icon,
                             String env,String commonFunctionsIcon, String label);

    /**
     * 删除菜单或者权限
     *
     * @param id
     * @return
     */
    Boolean deleteMenuOrAuthById(Long id);

    /**
     * 移动菜单节点
     * @param id
     * @param parentId
     * @return
     */
    Boolean moveMenu(Long id, Long parentId);

    /**
     * 节点排序
     * @param idList
     * @return
     */
    Boolean sortMenu(List<Long> idList);

    //
    Boolean addAppMenu(AppMenuAddRequest request);

    Boolean modifyAppMenu(AppMenuModifyRequest request);

    Boolean deleteAppMenu(AppMenuDeleteRequest request);

    /**
     * 获取移动端菜单
     * @return
     */
    List<AppMenuDTO> ListAppMenu();


    List<RoleMenuAuthVO> getAppMenuList(String enterpriseId, String roleId, CurrentUser currentUser);

    List<MenuTreeVO> getAppAuthList(String enterpriseId, String userId, CurrentUser user,Boolean isOld);

    List<RoleMenuAuthVO> getAllMenuList(String enterpriseId, List<SysMenuVO> list);

    /**
     * 获取企业下的所有菜单
     * @param enterpriseId
     * @return
     */
    List<SysMenuDO> getEnterpriseAllMenus(String enterpriseId);
}
