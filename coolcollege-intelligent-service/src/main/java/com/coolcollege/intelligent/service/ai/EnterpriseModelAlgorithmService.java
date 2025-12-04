package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.model.ai.EnterpriseModelAlgorithmDTO;

import java.util.List;

/**
 * <p>
 * 企业AI算法模型库服务接口
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
public interface EnterpriseModelAlgorithmService {

    List<EnterpriseModelAlgorithmDTO> list(String enterpriseId, Long groupId);

    EnterpriseModelAlgorithmDTO detail(String enterpriseId, Long sceneId);

    /**
     * 根据主键更新企业模型算法记录
     *
     * @param record 企业模型算法实体
     * @return 影响行数
     */
    void update(EnterpriseModelAlgorithmDTO record);


    /**
     * 根据主键更新企业模型算法记录
     *
     * @return 影响行数
     */
    void disable(String enterpriseId, Long sceneId);


    void enable(String enterpriseId, Long sceneId);

    List<EnterpriseModelAlgorithmDTO> enterpriseAlgorithmList(String enterpriseId, Long groupId);
}