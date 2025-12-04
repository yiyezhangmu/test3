package com.coolcollege.intelligent.dao.ai;

import com.coolcollege.intelligent.model.ai.entity.AiModelGroupDO;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-09-25 03:51
 */
public interface AiModelGroupMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-09-25 03:51
     */
    int insertSelective(AiModelGroupDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-09-25 03:51
     */
    int updateByPrimaryKeySelective(AiModelGroupDO record);

    /**
     *
     * 根据主键查询
     * dateTime:2025-09-25 03:51
     */
    List<AiModelGroupDO> list();

}