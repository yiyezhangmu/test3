package com.coolcollege.intelligent.dao.bosspackage;

import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:10
 */
@Mapper
public interface EnterprisePackageMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:10
     */
    Long insertSelective(EnterprisePackageDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:10
     */
    EnterprisePackageDO selectByPrimaryKey(Long id);

    /**
     * 通过名称获取套餐
     * @param packageName
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO>
     * @date: 2022/3/29 15:08
     */
    List<EnterprisePackageDO> selectByPackageName(@Param("packageName") String packageName);

    /**
     * 获得所有企业套餐
     * @param
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO>
     * @date: 2022/3/24 14:12
     */
    List<EnterprisePackageVO> selectAll();

    /**
     * 获得套餐列表
     * @param status
     * @author: xugangkun
     * @return
     * @date: 2022/3/23 10:16
     */
    List<EnterprisePackageVO> getPackageList(@Param("status") String status);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:10
     */
    int updateByPrimaryKeySelective(EnterprisePackageDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:10
     */
    int deleteByPrimaryKey(Long id);
}