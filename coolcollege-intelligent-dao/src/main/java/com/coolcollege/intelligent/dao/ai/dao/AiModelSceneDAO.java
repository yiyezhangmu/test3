package com.coolcollege.intelligent.dao.ai.dao;

import com.coolcollege.intelligent.dao.ai.AiModelSceneMapper;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AI模型场景
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiModelSceneDAO {
    private final AiModelSceneMapper aiModelSceneMapper;


    public List<AiModelSceneDO> list(Long groupId, List<Long> idList) {
        return aiModelSceneMapper.list(groupId, idList);
    }

    /**
     * 插入场景记录
     * @param record 场景实体
     * @return 影响行数
     */
    public int insertSelective(AiModelSceneDO record) {
        return aiModelSceneMapper.insertSelective(record);
    }

    /**
     * 根据主键更新场景记录
     * @param record 场景实体
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiModelSceneDO record) {
        return aiModelSceneMapper.updateByPrimaryKeySelective(record);
    }


    public AiModelSceneDO selectById(Long id) {
        return aiModelSceneMapper.selectById(id);
    }


    public AiModelSceneDO selectCorrectByModelCode(String modelCode) {
        return aiModelSceneMapper.selectCorrectByModelCode(modelCode);
    }

}