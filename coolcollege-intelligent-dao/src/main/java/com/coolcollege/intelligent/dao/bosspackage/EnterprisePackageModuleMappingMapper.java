package com.coolcollege.intelligent.dao.bosspackage;

import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageModuleMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:12
 */
@Mapper
public interface EnterprisePackageModuleMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:12
     */
    int insertSelective(EnterprisePackageModuleMappingDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:12
     */
    EnterprisePackageModuleMappingDO selectByPrimaryKey(Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:12
     */
    int updateByPrimaryKeySelective(EnterprisePackageModuleMappingDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:12
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据套餐id删除
     * @param packageId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/24 16:50
     */
    void deleteByPackageId(@Param("packageId") Long packageId);

    /**
     * 批量添加业务模块菜单映射
     * @param packageId
     * @param moduleIds
     * @param createUserId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 16:18
     */
    void batchInsert(@Param("packageId") Long packageId, @Param("moduleIds") List<Long> moduleIds, @Param("createUserId") String createUserId);

    /**
     * 通过套餐id获得业务模块列表id
     * @param packageId
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/24 17:18
     */
    List<Long> selectModuleIdsByPackageId(@Param("packageId") Long packageId);

    /**
     * 查看使用该业务模块的套餐
     * @param moduleId
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/24 17:18
     */
    List<Long> selectPackageIdsByModuleId(@Param("moduleId") Long moduleId);
}