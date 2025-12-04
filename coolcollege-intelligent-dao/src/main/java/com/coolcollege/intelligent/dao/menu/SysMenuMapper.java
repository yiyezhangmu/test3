package com.coolcollege.intelligent.dao.menu;

import com.coolcollege.intelligent.model.menu.SysMenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色
 *
 * @author shoul
 */
@Mapper
public interface SysMenuMapper {


    /**
     * 查询菜单（从平台库查询）
     * @param parentIds
     * @param platformType
     * @param env
     * @return
     */
    List<SysMenuDO> selectMenuAll(@Param("list") List<Long> parentIds,
                                   @Param("platformType") String platformType,
                                  @Param("env") String env);


    /**
     * 获得指定类型的菜单id
     * @param platformType
     * @param menuType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 14:57
     */
    List<SysMenuDO> selectMenuByMenuType(@Param("platformType") String platformType, @Param("menuType") Integer menuType, @Param("env") String env);

    /**
     * 根据id列表和平台类型获得菜单
     * @param ids
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<SysMenuDO> selectMenuByIds(@Param("ids") List<Long> ids, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 根据id列表和平台类型获得菜单
     * @param ids
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<Long> selectParentIdByIds(@Param("ids") List<Long> ids, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 根据parentId获得菜单列表
     * @param parentIds
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<Long> selectByIdByParentId(@Param("parentIds") List<Long> parentIds, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 获得指定类型的菜单id
     * @param platformType
     * @param menuType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 14:57
     */
    List<SysMenuDO> selectMenuByMenuTypeOld(@Param("platformType") String platformType, @Param("menuType") Integer menuType, @Param("env") String env);

    /**
     * 根据id列表和平台类型获得菜单
     * @param ids
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<SysMenuDO> selectMenuByIdsOld(@Param("ids") List<Long> ids, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 根据id列表和平台类型获得菜单
     * @param ids
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<Long> selectParentIdByIdsOld(@Param("ids") List<Long> ids, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 根据parentId获得菜单列表
     * @param parentIds
     * @param platformType
     * @param env
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.menu.SysMenuDO>
     * @date: 2022/3/28 15:01
     */
    List<Long> selectByIdByParentIdOld(@Param("parentIds") List<Long> parentIds, @Param("platformType") String platformType, @Param("env") String env);

    /**
     * 查询菜单（从平台库查询）
     * @param parentIds
     * @param platformType
     * @param env
     * @return
     */
    List<SysMenuDO> selectMenuAllOld( @Param("list") List<Long> parentIds,
                                   @Param("platformType") String platformType,
                                      @Param("env") String env);

    /**
     * 查询菜单（从平台库查询）
     * @param parentIds
     * @param platformType
     * @return
     */
    List<SysMenuDO> selectMenuAllOld( @Param("list") List<Long> parentIds,
                                   @Param("platformType") String platformType);

    /**
     * 插入菜单
     * @param sysMenuDO
     */
    void insertMenu(@Param("menuDO")SysMenuDO sysMenuDO);

    /**
     * 修改菜单
     * @param sysMenuDO
     */
    void updateMenu(@Param("menuDO")SysMenuDO sysMenuDO);

    /**
     * 批量删除菜单
     * @param idList
     */
    void batchDeleteMenu(@Param("list") List<Long> idList);

    /**
     * 更新菜单顺序
     * @param sysMenuDOList
     */
    void updateMenuSort(@Param("list")List<SysMenuDO> sysMenuDOList);

    void updateMenuMove(@Param("id")Long id ,@Param("parentId")Long parentId);

    SysMenuDO selectMenu(@Param("id")Long id);

    Integer selectMaxSort();


}
