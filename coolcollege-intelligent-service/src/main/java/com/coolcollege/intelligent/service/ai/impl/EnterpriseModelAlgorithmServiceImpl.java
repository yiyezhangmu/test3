package com.coolcollege.intelligent.service.ai.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.ai.dao.AiModelLibraryDAO;
import com.coolcollege.intelligent.dao.ai.dao.AiModelSceneDAO;
import com.coolcollege.intelligent.dao.ai.dao.EnterpriseModelAlgorithmDAO;
import com.coolcollege.intelligent.model.ai.EnterpriseModelAlgorithmDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import com.coolcollege.intelligent.model.ai.entity.EnterpriseModelAlgorithmDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.service.ai.EnterpriseModelAlgorithmService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 企业AI算法模型库服务实现类
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@Service
public class EnterpriseModelAlgorithmServiceImpl implements EnterpriseModelAlgorithmService {

    @Autowired
    private EnterpriseModelAlgorithmDAO enterpriseModelAlgorithmDAO;

    @Resource
    private AiModelSceneDAO aiModelSceneDAO;
    @Resource
    private AiModelLibraryDAO aiModelLibraryDAO;

    @Override
    public List<EnterpriseModelAlgorithmDTO> list(String enterpriseId, Long groupId) {
        List<AiModelSceneDO> aiModelSceneDOList = aiModelSceneDAO.list(groupId, null);
        List<EnterpriseModelAlgorithmDO> modelAlgorithmDOList = enterpriseModelAlgorithmDAO.list(enterpriseId, null);
        Map<Long, EnterpriseModelAlgorithmDO> modelAlgorithmMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(modelAlgorithmDOList)) {
            modelAlgorithmMap = modelAlgorithmDOList.stream().collect(Collectors.toMap(EnterpriseModelAlgorithmDO::getSceneId, algorithmDO -> algorithmDO));
        }
        List<String> modelCodeList = aiModelSceneDOList.stream()
                .map(AiModelSceneDO::getModelCode)
                .collect(Collectors.toList());

        Map<String, AiModelLibraryDO> modelLibraryDOMap = aiModelLibraryDAO.getModelMapByCodes(modelCodeList);

        Map<Long, EnterpriseModelAlgorithmDO> finalModelAlgorithmMap = modelAlgorithmMap;
        return aiModelSceneDOList.stream()
                .map(aiModelSceneDO -> EnterpriseModelAlgorithmDTO.builder()
                        .sceneId(aiModelSceneDO.getId())
                        .sceneName(aiModelSceneDO.getSceneName())
                        .modelName(aiModelSceneDO.getModelName())
                        .modelCode(aiModelSceneDO.getModelCode())
                        .systemPrompt(aiModelSceneDO.getSystemPrompt())
                        .scenePic(aiModelSceneDO.getScenePic())
                        .userPrompt(aiModelSceneDO.getUserPrompt())
                        .standardPic(aiModelSceneDO.getStandardPic())
                        .groupId(aiModelSceneDO.getGroupId())
                        .groupName(aiModelSceneDO.getGroupName())
                        .standardDesc(aiModelSceneDO.getStandardDesc())
                        .editFlag(Constants.SHUZI_CODE_LIST.contains(aiModelSceneDO.getModelCode())
                        || modelLibraryDOMap.get(aiModelSceneDO.getModelCode()) != null && modelLibraryDOMap.get(aiModelSceneDO.getModelCode()).isSupportCustomPrompt())
                        .supportCustomPrompt(modelLibraryDOMap.get(aiModelSceneDO.getModelCode()) != null && modelLibraryDOMap.get(aiModelSceneDO.getModelCode()).isSupportCustomPrompt())
                        .algorithmStatus(finalModelAlgorithmMap.get(aiModelSceneDO.getId()) == null ? 0 :
                                finalModelAlgorithmMap.get(aiModelSceneDO.getId()).getAlgorithmStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public EnterpriseModelAlgorithmDTO detail(String enterpriseId, Long sceneId) {
        EnterpriseModelAlgorithmDO modelAlgorithmDO = enterpriseModelAlgorithmDAO.detail(enterpriseId, sceneId);
        AiModelSceneDO modelSceneDO = aiModelSceneDAO.selectById(sceneId);
        AiModelLibraryDO modelLibraryDO = aiModelLibraryDAO.getModelByCode(modelSceneDO.getModelCode());
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = EnterpriseModelAlgorithmDTO.builder()
                .sceneId(modelSceneDO.getId())
                .sceneName(modelSceneDO.getSceneName())
                .modelName(modelSceneDO.getModelName())
                .modelCode(modelSceneDO.getModelCode())
                .groupId(modelSceneDO.getGroupId())
                .groupName(modelSceneDO.getGroupName())
                .scenePic(modelSceneDO.getScenePic())
                .standardDesc(modelSceneDO.getStandardDesc())
                .systemPrompt(modelSceneDO.getSystemPrompt())
                .userPrompt(modelSceneDO.getUserPrompt())
                .defaultUserPrompt(modelSceneDO.getUserPrompt())
                .standardPic(modelSceneDO.getStandardPic())
                .editFlag(Constants.SHUZI_CODE_LIST.contains(modelSceneDO.getModelCode()))
                .algorithmStatus(0)
                .build();
        if (modelAlgorithmDO != null) {
            if (StringUtils.isNotBlank(modelAlgorithmDO.getUserPrompt())) {
                modelAlgorithmDTO.setUserPrompt(modelAlgorithmDO.getUserPrompt());
            }
            modelAlgorithmDTO.setSpecialPrompt(modelAlgorithmDO.getSpecialPrompt());
            if (StringUtils.isNotBlank(modelAlgorithmDO.getStandardPic())) {
                modelAlgorithmDTO.setStandardPic(modelAlgorithmDO.getStandardPic());
            }
            modelAlgorithmDTO.setAlgorithmStatus(modelAlgorithmDO.getAlgorithmStatus());
            modelAlgorithmDTO.setProcess(StringUtils.isNotBlank(modelAlgorithmDO.getNodeInfo()) ? JSONObject.parseArray(modelAlgorithmDO.getNodeInfo(), TaskProcessDTO.class) : new ArrayList<>());
            modelAlgorithmDTO.setExpiryPolicy(modelAlgorithmDO.getExpiryPolicy());
            modelAlgorithmDTO.setExpiryTimes(modelAlgorithmDO.getExpiryTimes());
        }
        if(modelLibraryDO != null){
            modelAlgorithmDTO.setSupportCustomPrompt(modelLibraryDO.isSupportCustomPrompt());
        }
        return modelAlgorithmDTO;
    }

    @Override
    public void update(EnterpriseModelAlgorithmDTO record) {
        EnterpriseModelAlgorithmDO modelAlgorithmDO = enterpriseModelAlgorithmDAO.detail(record.getEnterpriseId(), record.getSceneId());
        AiModelSceneDO modelSceneDO = aiModelSceneDAO.selectById(record.getSceneId());

        EnterpriseModelAlgorithmDO modelAlgorithm = EnterpriseModelAlgorithmDO.builder()
                .enterpriseId(record.getEnterpriseId())
                .sceneId(record.getSceneId())
                .sceneName(modelSceneDO.getSceneName())
                .modelName(modelSceneDO.getModelName())
                .modelCode(modelSceneDO.getModelCode())
                .systemPrompt(modelSceneDO.getSystemPrompt())
                .userPrompt(record.getUserPrompt())
                .specialPrompt(record.getSpecialPrompt())
                .standardPic(record.getStandardPic())
                .remark(record.getRemark())
                .algorithmStatus(record.getAlgorithmStatus())
                .expiryTimes(record.getExpiryTimes())
                .expiryPolicy(record.getExpiryPolicy())
                .build();
        if (modelAlgorithmDO != null) {
            modelAlgorithm.setId(modelAlgorithmDO.getId());
            if(CollectionUtils.isNotEmpty(record.getProcess())){
                modelAlgorithm.setNodeInfo(JSONObject.toJSONString(record.getProcess()));
            }else {
                modelAlgorithm.setNodeInfo("");
            }
            enterpriseModelAlgorithmDAO.updateByPrimaryKeySelective(modelAlgorithm);
        } else {
            enterpriseModelAlgorithmDAO.insertSelective(modelAlgorithm);
        }
    }

    @Override
    public void disable(String enterpriseId, Long sceneId) {
        EnterpriseModelAlgorithmDO modelAlgorithmDO = enterpriseModelAlgorithmDAO.detail(enterpriseId, sceneId);
        if (modelAlgorithmDO != null) {
            modelAlgorithmDO.setAlgorithmStatus(0);
            enterpriseModelAlgorithmDAO.updateByPrimaryKeySelective(modelAlgorithmDO);
        }
    }

    @Override
    public void enable(String enterpriseId, Long sceneId) {
        AiModelSceneDO modelSceneDO = aiModelSceneDAO.selectById(sceneId);
        EnterpriseModelAlgorithmDO modelAlgorithmUpdate = enterpriseModelAlgorithmDAO.detail(enterpriseId, sceneId);
        if (modelAlgorithmUpdate == null) {
            EnterpriseModelAlgorithmDO modelAlgorithmDO = EnterpriseModelAlgorithmDO.builder()
                    .enterpriseId(enterpriseId)
                    .sceneId(sceneId)
                    .sceneName(modelSceneDO.getSceneName())
                    .modelName(modelSceneDO.getModelName())
                    .modelCode(modelSceneDO.getModelCode())
                    .systemPrompt(modelSceneDO.getSystemPrompt())
                    .userPrompt(modelSceneDO.getUserPrompt())
                    .standardPic(modelSceneDO.getStandardPic())
                    .algorithmStatus(1)
                    .build();
            enterpriseModelAlgorithmDAO.insertSelective(modelAlgorithmDO);
        } else {
            modelAlgorithmUpdate.setAlgorithmStatus(1);
            enterpriseModelAlgorithmDAO.updateByPrimaryKeySelective(modelAlgorithmUpdate);
        }
    }

    @Override
    public List<EnterpriseModelAlgorithmDTO> enterpriseAlgorithmList(String enterpriseId, Long groupId) {
        List<AiModelSceneDO> aiModelSceneDOList = aiModelSceneDAO.list(groupId, null);
        if(CollectionUtils.isEmpty(aiModelSceneDOList)){
            return new ArrayList<>();
        }
        List<Long> sceneIdList = aiModelSceneDOList.stream()
                .map(AiModelSceneDO::getId)
                .collect(Collectors.toList());
        List<EnterpriseModelAlgorithmDO> modelAlgorithmDOList = enterpriseModelAlgorithmDAO.list(enterpriseId, sceneIdList);

        if(CollectionUtils.isEmpty(modelAlgorithmDOList)){
            return new ArrayList<>();
        }
        modelAlgorithmDOList = modelAlgorithmDOList.stream()
                .filter(modelAlgorithmDO -> modelAlgorithmDO.getAlgorithmStatus() == 1)
                .collect(Collectors.toList());
        Map<Long, AiModelSceneDO> sceneMap = aiModelSceneDOList.stream()
                .collect(Collectors.toMap(AiModelSceneDO::getId, Function.identity()));

        List<String> modelCodeList = aiModelSceneDOList.stream()
                .map(AiModelSceneDO::getModelCode)
                .collect(Collectors.toList());

        Map<String, AiModelLibraryDO> modelLibraryDOMap = aiModelLibraryDAO.getModelMapByCodes(modelCodeList);

        return modelAlgorithmDOList.stream()
                .map(modelAlgorithmDO -> {
                    AiModelSceneDO scene = sceneMap.get(modelAlgorithmDO.getSceneId());
                    AiModelLibraryDO modelLibraryDO = modelLibraryDOMap.get(modelAlgorithmDO.getModelCode());
                    return EnterpriseModelAlgorithmDTO.builder()
                            .sceneId(modelAlgorithmDO.getSceneId())
                            .sceneName(modelAlgorithmDO.getSceneName())
                            .modelName(modelAlgorithmDO.getModelName())
                            .modelCode(modelAlgorithmDO.getModelCode())
                            .systemPrompt(getValueOrDefault(modelAlgorithmDO.getSystemPrompt(), scene, AiModelSceneDO::getSystemPrompt))
                            .userPrompt(getValueOrDefault(modelAlgorithmDO.getUserPrompt(), scene, AiModelSceneDO::getUserPrompt))
                            .standardPic(getValueOrDefault(modelAlgorithmDO.getStandardPic(), scene, AiModelSceneDO::getStandardPic))
                            .standardDesc(scene.getStandardDesc())
                            .scenePic(scene.getScenePic())
                            .algorithmStatus(modelAlgorithmDO.getAlgorithmStatus())
                            .enterpriseId(modelAlgorithmDO.getEnterpriseId())
                            .specialPrompt(modelAlgorithmDO.getSpecialPrompt())
                            .editFlag(Constants.SHUZI_CODE_LIST.contains(modelAlgorithmDO.getModelCode()) || modelLibraryDO.isSupportCustomPrompt())
                            .supportCustomPrompt(modelLibraryDO.isSupportCustomPrompt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String getValueOrDefault(String primaryValue, AiModelSceneDO scene, Function<AiModelSceneDO, String> fallbackGetter) {
        if (StringUtils.isNotBlank(primaryValue)) {
            return primaryValue;
        }
        return scene != null ? fallbackGetter.apply(scene) : null;
    }
}