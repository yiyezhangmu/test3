package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2024-09-06 11:20
 */
public interface TbWxGroupConfigMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-06 11:20
     */
    int insertSelective(@Param("record") TbWxGroupConfigDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-06 11:20
     */
    int updateByPrimaryKeySelective(@Param("record") TbWxGroupConfigDO record, @Param("enterpriseId") String enterpriseId);


    /**
     * 根据id查询
     * @param id
     * @param enterpriseId
     * @return
     */
    TbWxGroupConfigDO getById(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 查询列表
     * @param enterpriseId
     * @return
     */
    Page<TbWxGroupConfigDO> getGroupConfigList(@Param("enterpriseId") String enterpriseId);
}