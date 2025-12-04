package com.coolcollege.intelligent.dao.bosspackage;

import com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:12
 */
@Mapper
public interface BusinessModuleMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:12
     */
    Long insertSelective(BusinessModuleDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:12
     */
    BusinessModuleDO selectByPrimaryKey(Long id);

    /**
     * 通过业务模块名称获得业务模块
     * @param moduleName
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO
     * @date: 2022/3/29 15:01
     */
    List<BusinessModuleDO> selectByModuleName(@Param("moduleName") String moduleName);


    /**
     * 获得业务模块列表
     * @param status
     * @author: xugangkun
     * @return
     * @date: 2022/3/23 10:16
     */
    List<BusinessModuleVO> getModuleList(@Param("status") String status);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:12
     */
    int updateByPrimaryKeySelective(BusinessModuleDO record);

    /**
     * 根据主键禁用
     * @param status
     * @param moduleId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 14:22
     */
    void updateModuleStatus(@Param("status") String status, @Param("moduleId") Long moduleId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:12
     */
    int deleteByPrimaryKey(Long id);
}