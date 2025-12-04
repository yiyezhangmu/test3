package com.coolcollege.intelligent.dao.ai.dao;

import com.coolcollege.intelligent.dao.ai.AiModelGroupMapper;
import com.coolcollege.intelligent.model.ai.entity.AiModelGroupDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AI算法分组
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiModelGroupDAO {
    private final AiModelGroupMapper aiModelGroupMapper;



    public List<AiModelGroupDO> list() {
        return aiModelGroupMapper.list();
    }

    /**
     * 插入分组记录
     * @param record 分组实体
     * @return 影响行数
     */
    public int insertSelective(AiModelGroupDO record) {
        return aiModelGroupMapper.insertSelective(record);
    }

    /**
     * 根据主键更新分组记录
     * @param record 分组实体
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiModelGroupDO record) {
        return aiModelGroupMapper.updateByPrimaryKeySelective(record);
    }
}