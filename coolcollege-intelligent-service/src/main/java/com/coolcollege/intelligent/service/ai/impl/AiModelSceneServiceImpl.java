package com.coolcollege.intelligent.service.ai.impl;

import com.coolcollege.intelligent.dao.ai.dao.AiModelGroupDAO;
import com.coolcollege.intelligent.dao.ai.dao.AiModelSceneDAO;
import com.coolcollege.intelligent.dao.ai.dao.EnterpriseModelAlgorithmDAO;
import com.coolcollege.intelligent.model.ai.AiModelGroupVO;
import com.coolcollege.intelligent.model.ai.AiModelSceneDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelGroupDO;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.service.ai.AiModelSceneService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * AI模型场景服务实现类
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@Service
public class AiModelSceneServiceImpl implements AiModelSceneService {
    @Resource
    private AiModelGroupDAO aiModelGroupDAO;

    @Resource
    private AiModelSceneDAO aiModelSceneDAO;

    @Resource
    private EnterpriseModelAlgorithmDAO enterpriseModelAlgorithmDAO;

    @Override
    public List<AiModelSceneVO> list(Long groupId, List<Long> idList) {
        List<AiModelSceneDO> aiModelSceneDOList = aiModelSceneDAO.list(groupId, idList);
        List<AiModelSceneVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(aiModelSceneDOList)) {
            return resultList;
        }
        aiModelSceneDOList.forEach(aiModelSceneDO -> {
            resultList.add(AiModelSceneVO.builder()
                    .sceneId(aiModelSceneDO.getId())
                    .sceneName(aiModelSceneDO.getSceneName())
                    .scenePic(aiModelSceneDO.getScenePic())
                    .modelName(aiModelSceneDO.getModelName())
                    .modelCode(aiModelSceneDO.getModelCode())
                    .groupId(aiModelSceneDO.getGroupId())
                    .groupName(aiModelSceneDO.getGroupName())
                    .standardPic(aiModelSceneDO.getStandardPic())
                    .createTime(aiModelSceneDO.getCreateTime())
                    .updateTime(aiModelSceneDO.getUpdateTime())
                    .standardDesc(aiModelSceneDO.getStandardDesc())
                    .systemPrompt(aiModelSceneDO.getSystemPrompt())
                    .userPrompt(aiModelSceneDO.getUserPrompt())
                    .build()
            );
        });
        return resultList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAiScene(AiModelSceneDTO aiModelSceneDTO) {
        AiModelSceneDO aiModelSceneDO = AiModelSceneDO.builder()
                .id(aiModelSceneDTO.getSceneId())
                .sceneName(aiModelSceneDTO.getSceneName())
                .scenePic(aiModelSceneDTO.getScenePic())
                .modelName(aiModelSceneDTO.getModelName())
                .modelCode(aiModelSceneDTO.getModelCode())
                .groupId(aiModelSceneDTO.getGroupId())
                .groupName(aiModelSceneDTO.getGroupName())
                .standardPic(aiModelSceneDTO.getStandardPic())
                .standardDesc(aiModelSceneDTO.getStandardDesc())
                .systemPrompt(aiModelSceneDTO.getSystemPrompt())
                .userPrompt(aiModelSceneDTO.getUserPrompt())
                .scenePic(aiModelSceneDTO.getScenePic())
                .build();
        aiModelSceneDAO.updateByPrimaryKeySelective(aiModelSceneDO);

        enterpriseModelAlgorithmDAO.updateBySceneId(aiModelSceneDO.getId(), aiModelSceneDO.getSceneName(), aiModelSceneDO.getModelName(), aiModelSceneDO.getModelCode());
    }

    @Override
    public void addAiScene(AiModelSceneDTO aiModelSceneDTO) {
        AiModelSceneDO aiModelSceneDO = AiModelSceneDO.builder()
                .id(aiModelSceneDTO.getSceneId())
                .sceneName(aiModelSceneDTO.getSceneName())
                .scenePic(aiModelSceneDTO.getScenePic())
                .modelName(aiModelSceneDTO.getModelName())
                .modelCode(aiModelSceneDTO.getModelCode())
                .groupId(aiModelSceneDTO.getGroupId())
                .groupName(aiModelSceneDTO.getGroupName())
                .standardPic(aiModelSceneDTO.getStandardPic())
                .standardDesc(aiModelSceneDTO.getStandardDesc())
                .systemPrompt(aiModelSceneDTO.getSystemPrompt())
                .userPrompt(aiModelSceneDTO.getUserPrompt())
                .scenePic(aiModelSceneDTO.getScenePic())
                .build();
        aiModelSceneDAO.insertSelective(aiModelSceneDO);
    }

    @Override
    public AiModelSceneVO detail(Long id) {
        AiModelSceneDO aiModelSceneDO = aiModelSceneDAO.selectById(id);
        if (aiModelSceneDO != null) {
            return AiModelSceneVO.builder()
                    .sceneId(aiModelSceneDO.getId())
                    .sceneName(aiModelSceneDO.getSceneName())
                    .scenePic(aiModelSceneDO.getScenePic())
                    .modelName(aiModelSceneDO.getModelName())
                    .modelCode(aiModelSceneDO.getModelCode())
                    .groupId(aiModelSceneDO.getGroupId())
                    .groupName(aiModelSceneDO.getGroupName())
                    .standardPic(aiModelSceneDO.getStandardPic())
                    .createTime(aiModelSceneDO.getCreateTime())
                    .updateTime(aiModelSceneDO.getUpdateTime())
                    .standardDesc(aiModelSceneDO.getStandardDesc())
                    .systemPrompt(aiModelSceneDO.getSystemPrompt())
                    .userPrompt(aiModelSceneDO.getUserPrompt())
                    .build();
        }
        return null;
    }

    @Override
    public List<AiModelGroupVO> groupList() {
        List<AiModelGroupDO> aiModelGroupDOList = aiModelGroupDAO.list();
        if (CollectionUtils.isEmpty(aiModelGroupDOList)) {
            return new ArrayList<>();
        }
        List<AiModelGroupVO> resultList = new ArrayList<>();
        aiModelGroupDOList.forEach(aiModelGroupDO -> {
            resultList.add(AiModelGroupVO.builder()
                    .id(aiModelGroupDO.getId())
                    .groupName(aiModelGroupDO.getGroupName())
                    .remark(aiModelGroupDO.getRemark())
                    .build());
        });
        return resultList;
    }

    @Override
    public AiModelSceneDO selectCorrectByModelCode(String modelCode) {
        return aiModelSceneDAO.selectCorrectByModelCode(modelCode);
    }
}