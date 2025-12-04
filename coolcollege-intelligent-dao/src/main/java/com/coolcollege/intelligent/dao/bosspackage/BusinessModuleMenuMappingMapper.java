package com.coolcollege.intelligent.dao.bosspackage;

import com.coolcollege.intelligent.model.bosspackage.BusinessModuleMenuMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:12
 */
@Mapper
public interface BusinessModuleMenuMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:12
     */
    int insertSelective(BusinessModuleMenuMappingDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:12
     */
    BusinessModuleMenuMappingDO selectByPrimaryKey(Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:12
     */
    int updateByPrimaryKeySelective(BusinessModuleMenuMappingDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:12
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据模块id删除菜单
     * @param moduleId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 15:48
     */
    void deleteByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 批量添加业务模块菜单映射
     * @param moduleId
     * @param menuIds
     * @param platform
     * @param createUserId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 16:18
     */
    void batchInsert(@Param("moduleId") Long moduleId, @Param("menuIds") List<Long> menuIds, @Param("platform") String platform,
                     @Param("createUserId") String createUserId);

    /**
     * 根据模块id获得菜单列表
     * @param moduleId
     * @param platformType
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/28 10:59
     */
    List<Long> selectMenuIdsByModuleId(@Param("moduleId") Long moduleId, @Param("platformType") String platformType);

    /**
     * 根据模块id列表获得菜单列表
     * @param moduleIds
     * @param platformType
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/28 11:00
     */
    List<Long> selectMenuIdsByModuleIds(@Param("moduleIds") List<Long> moduleIds, @Param("platformType") String platformType);
}