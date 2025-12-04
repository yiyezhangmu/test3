package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseOpenLeaveInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-08-17 07:49
 */
public interface EnterpriseOpenLeaveInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-08-17 07:49
     */
    int insertSelective(EnterpriseOpenLeaveInfoDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-17 07:49
     */
    EnterpriseOpenLeaveInfoDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-08-17 07:49
     */
    int updateByPrimaryKeySelective(EnterpriseOpenLeaveInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-08-17 07:49
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    EnterpriseOpenLeaveInfoDO selectByEnterpriseId(@Param("enterpriseId") String enterpriseId);

    List<EnterpriseOpenLeaveInfoDO> listByEnterpriseIds(@Param("enterpriseIds") List<String> enterpriseIds);

}