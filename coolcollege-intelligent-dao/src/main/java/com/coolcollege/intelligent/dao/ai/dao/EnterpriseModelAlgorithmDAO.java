package com.coolcollege.intelligent.dao.ai.dao;

import com.coolcollege.intelligent.dao.ai.EnterpriseModelAlgorithmMapper;
import com.coolcollege.intelligent.model.ai.entity.EnterpriseModelAlgorithmDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 企业AI算法模型库
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class EnterpriseModelAlgorithmDAO {
    private final EnterpriseModelAlgorithmMapper enterpriseModelAlgorithmMapper;

    /**
     * 插入企业模型算法记录
     *
     * @param record 企业模型算法实体
     * @return 影响行数
     */
    public int insertSelective(EnterpriseModelAlgorithmDO record) {
        return enterpriseModelAlgorithmMapper.insertSelective(record);
    }

    /**
     * 根据主键更新企业模型算法记录
     *
     * @param record 企业模型算法实体
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(EnterpriseModelAlgorithmDO record) {
        return enterpriseModelAlgorithmMapper.updateByPrimaryKeySelective(record);
    }

    public int deleteByPrimaryKey(Long id) {
        return enterpriseModelAlgorithmMapper.deleteByPrimaryKey(id);
    }

    public List<EnterpriseModelAlgorithmDO> list(String enterpriseId, List<Long> sceneIdList) {
        return enterpriseModelAlgorithmMapper.list(enterpriseId, sceneIdList);
    }

    public EnterpriseModelAlgorithmDO detail(String enterpriseId, Long sceneId) {
        return enterpriseModelAlgorithmMapper.detail(enterpriseId, sceneId);
    }

    public int updateBySceneId(Long sceneId, String sceneName, String modelName, String modelCode) {
        return enterpriseModelAlgorithmMapper.updateBySceneId(sceneId, sceneName, modelName, modelCode);
    }
}