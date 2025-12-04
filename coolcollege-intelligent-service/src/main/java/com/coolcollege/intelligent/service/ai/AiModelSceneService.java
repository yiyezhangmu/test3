package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.model.ai.AiModelGroupVO;
import com.coolcollege.intelligent.model.ai.AiModelSceneDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * AI模型场景服务接口
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
public interface AiModelSceneService {

    List<AiModelSceneVO> list(Long groupId, List<Long> idList);


    void updateAiScene(AiModelSceneDTO aiModelSceneDTO);

    void addAiScene(AiModelSceneDTO aiModelSceneDTO);

    AiModelSceneVO detail(Long id);

    List<AiModelGroupVO> groupList();

    AiModelSceneDO selectCorrectByModelCode(String modelCode);
}