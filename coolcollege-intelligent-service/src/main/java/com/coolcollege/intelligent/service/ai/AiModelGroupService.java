package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.model.ai.entity.AiModelGroupDO;

/**
 * <p>
 * AI算法分组服务接口
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
public interface AiModelGroupService {

    /**
     * 插入分组记录
     * @param record 分组实体
     * @return 影响行数
     */
    int insertSelective(AiModelGroupDO record);

    /**
     * 根据主键更新分组记录
     * @param record 分组实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(AiModelGroupDO record);
}