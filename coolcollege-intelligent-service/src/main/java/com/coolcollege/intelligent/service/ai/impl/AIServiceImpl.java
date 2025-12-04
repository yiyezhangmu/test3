package com.coolcollege.intelligent.service.ai.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.*;
import com.coolcollege.intelligent.model.ai.dto.InspectionInfoDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import com.coolcollege.intelligent.model.ai.vo.AIModelVO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.model.enums.AICommentStyleEnum;
import com.coolcollege.intelligent.model.enums.AIPlatformEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.service.ai.*;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum.AI_INSPECTION;

/**
 * <p>
 * AI服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {
    private final AIOpenFactory aiOpenFactory;
    private final RedisUtilPool redisUtilPool;
    private final AiModelLibraryService aiModelLibraryService;
    private final EnterpriseModelAlgorithmService enterpriseModelAlgorithmService;
    private final AiModelSceneService aiModelSceneService;

    @Override
    public List<AIModelVO> getAIModelList() {
        List<AiModelLibraryDO> list = aiModelLibraryService.getList(true, "model");
        return CollStreamUtil.toList(list, AIModelVO::convert);
    }

    @Override
    public AIResolveDTO aiPatrolResolve(String enterpriseId,
                                        AiModelLibraryDO aiModel,
                                        List<String> imageList,
                                        TbMetaStaTableColumnDO metaStaTableColumnDO,
                                        List<TbMetaColumnResultDO> resultDOList,
                                        String style) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        if (StringUtils.isBlank(style)) {
            style = AICommentStyleEnum.DETAIL.getStyle();
        }
        AiModelLibraryDO aiPlatform = aiModelLibraryService.getPlatformByCode(aiModel.getPlatformCode());
        log.info("AI巡店分析, enterpriseId:{}, metaColumnId:{}, aiModel:{}, model：{}", enterpriseId, metaStaTableColumnDO.getId(), aiModel.getName(), aiModel.getCode());
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AICommonPromptDTO patrolPrompt = getPatrolPrompt(enterpriseId, metaStaTableColumnDO.getAiCheckStdDesc(), style, metaStaTableColumnDO.getAiSceneId());
        String aiResult = aiResolve.aiResolve(enterpriseId, patrolPrompt, imageList, aiModel);
        return AIHelper.matchPatrolResult(aiResult, resultDOList, aiPlatform.getResultStrategy());
    }

    @Override
    public AiInspectionResult aiInspectionResolve(String enterpriseId, Long sceneId, List<String> imageList) {
        DataSourceHelper.reset();
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, sceneId);
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }

        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelAlgorithmDTO.getModelCode());
        AiModelLibraryDO aiPlatform = aiModelLibraryService.getPlatformByCode(aiModel.getPlatformCode());

        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AiModelSceneVO aiModelScene = aiModelSceneService.detail(sceneId);
        String userPrompt = aiModelScene.getUserPrompt();
        if(StringUtils.isNotBlank(modelAlgorithmDTO.getUserPrompt())){
            userPrompt = modelAlgorithmDTO.getUserPrompt();
        }
        AICommonPromptDTO patrolPrompt = new AICommonPromptDTO(modelAlgorithmDTO.getSystemPrompt(), null, userPrompt + modelAlgorithmDTO.getSpecialPrompt());
        log.info("AI巡店分析, enterpriseId:{}, sceneId:{}, model：{}, modelStrategy:{}, modelPrompt:{}", enterpriseId, sceneId, aiModel.getName(), aiModel.getResultStrategy(), JSONObject.toJSONString(patrolPrompt));
        String aiResult = aiResolve.aiResolve(enterpriseId, patrolPrompt, imageList, aiModel);
        log.info("aiInspectionResolve#AI巡检分析, enterpriseId:{}, sceneId:{}, model：{}, aiResult:{}", enterpriseId, sceneId, aiModel.getName(), aiResult);
        return AIHelper.matchInspectionResolve(aiResult, aiPlatform.getResultStrategy());
    }

    @Override
    public AIResolveDTO aiAsyncInspectionResolve(String enterpriseId, Long sceneId, List<String> imageList, Long inspectionPeriodId){
        DataSourceHelper.reset();
        EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(enterpriseId, sceneId);
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelAlgorithmDTO.getModelCode());
        AIResolveRequestDTO request = new AIResolveRequestDTO();
        InspectionInfoDTO inspectionInfoDTO = new InspectionInfoDTO();
        inspectionInfoDTO.setImageList(imageList);
        inspectionInfoDTO.setBusinessId(String.valueOf(inspectionPeriodId));
        inspectionInfoDTO.setModelCode(aiModel.getCode());
        inspectionInfoDTO.setStandardPic(modelAlgorithmDTO.getStandardPic());
        request.setInspectionInfoDTO(inspectionInfoDTO);
        return aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode())).asyncAiResolve(enterpriseId, AI_INSPECTION, request);
    }


    @Override
    public AIResultDTO aiInspectionResolveTest(String enterpriseId, Long sceneId, List<String> imageList, String userPrompt, String modelCode) {
        DataSourceHelper.reset();
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(modelCode);
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AiModelSceneVO aiModelScene;
        String systemPrompt = "";
        if (sceneId != null) {
            aiModelScene = aiModelSceneService.detail(sceneId);
            systemPrompt = aiModelScene.getSystemPrompt();
        } else {
            AiModelSceneDO aiModelSceneDO = aiModelSceneService.selectCorrectByModelCode(modelCode);
            systemPrompt = aiModelSceneDO.getSystemPrompt();
        }
        AICommonPromptDTO patrolPrompt = new AICommonPromptDTO(systemPrompt, null, userPrompt);
        log.info("AI巡店分析, enterpriseId:{}, sceneId:{}, model：{}, modelStrategy:{}, modelPrompt:{}", enterpriseId, sceneId, aiModel.getName(), aiModel.getResultStrategy(), JSONObject.toJSONString(patrolPrompt));
        String aiResult = aiResolve.aiResolve(enterpriseId, patrolPrompt, imageList, aiModel);
        log.info("aiInspectionResolve#AI巡检分析, enterpriseId:{}, sceneId:{}, model：{}, aiResult:{}", enterpriseId, sceneId, aiModel.getName(), aiResult);
        return new AIResultDTO(aiResult);
    }

    @Override
    public AIResolveDTO aiPatrolResolve(String enterpriseId, AiResolveBusinessTypeEnum businessType, AiModelLibraryDO aiModel, AIResolveRequestDTO request){
        return aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode())).asyncAiResolve(enterpriseId, businessType, request);
    }

    @Override
    public AIResultDTO aiReportResolve(String enterpriseId, AiModelLibraryDO aiModel, String promptDimension, List<String> imageList) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        log.info("AI店报分析, enterpriseId:{}, aiModel:{}, model：{}", enterpriseId, aiModel.getName(), aiModel.getCode());
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AICommonPromptDTO reportPrompt = getReportPrompt(promptDimension);
        String aiResult = aiResolve.aiResolve(enterpriseId, reportPrompt, imageList, aiModel);
        return new AIResultDTO(aiResult);
    }

    @Override
    public String getAiCheckStaDesc(String enterpriseId, String aiModelCode, List<String> imageList, String text) {
        if (StringUtils.isBlank(aiModelCode)) aiModelCode = "qwen_vl";
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(aiModelCode);
        // 校验AI平台是否支持生成检查标准
        AiModelLibraryDO aiPlatform = aiModelLibraryService.getPlatformByCode(aiModel.getPlatformCode());
        if (!aiPlatform.isSupportCustomPrompt()) {
            throw new ServiceException(ErrorCodeEnum.AI_NO_SUPPORT_GEN_STA_DESC);
        }
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AICommonPromptDTO patrolPrompt = getAiCheckStdDescPrompt(text);
        log.info("生成检查标准, enterpriseId:{}, aiModel:{}, model：{}", enterpriseId, aiModel.getName(), aiModel.getCode());
        String result = aiResolve.aiResolve(enterpriseId, patrolPrompt, imageList, aiModel);
        String json = AIHelper.extractArrJson(result);
        try {
            JSONArray jsonArray = JSONObject.parseArray(json);
            if(Objects.nonNull(jsonArray) && !jsonArray.isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                int i = 1;
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(o));
                    stringBuilder.append(i).append("、").append(jsonObject.getString("dimension"))
                            .append(" :\n\t").append(jsonObject.getString("standDesc")).append("\n");
                    i++;
                }
                return stringBuilder.toString();
            }
        } catch (Exception exception) {
            log.info("resultStr:{}", result);
        }
        return result;
    }

    @Override
    public AiCommentAndScoreVO getPatrolAiCommentAndScore(String enterpriseId, AiCommentAndScoreBatchDTO dto) {
        if (com.alibaba.excel.util.CollectionUtils.isEmpty(dto.getImageList())) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        if (StringUtils.isBlank(dto.getAiModel())) dto.setAiModel("qwen_vl");
        AiModelLibraryDO aiModel = aiModelLibraryService.getModelByCode(dto.getAiModel());
        AiModelLibraryDO platform = aiModelLibraryService.getPlatformByCode(aiModel.getPlatformCode());
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AICommonPromptDTO prompt = getPatrolAiCommentAndScorePrompt(dto.getStandardDesc(), dto.getScore());
        log.info("生成巡店AI评论及分数, enterpriseId:{}, aiModel:{}, model：{}", enterpriseId, aiModel.getName(), aiModel.getCode());
        String result = aiResolve.aiResolve(enterpriseId, prompt, dto.getImageList(), aiModel);
        return AIHelper.convertAiResult(result, platform.getResultStrategy());
    }

    @Override
    public AIResolveDTO aiStoreWork(String enterpriseId, AiModelLibraryDO aiModel, List<String> imageList, TbMetaStaTableColumnDO metaStaTableColumnDO, List<TbMetaColumnResultDO> resultDOList, String style) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        AiModelLibraryDO aiPlatform = aiModelLibraryService.getPlatformByCode(aiModel.getPlatformCode());
        log.info("AI巡店分析, enterpriseId:{}, metaColumnId:{}, aiModel:{}, model：{}", enterpriseId, metaStaTableColumnDO.getId(), aiModel.getName(), aiModel.getCode());
        AIOpenService aiResolve = aiOpenFactory.getAIResolve(AIPlatformEnum.getByCode(aiModel.getPlatformCode()));
        AICommonPromptDTO patrolPrompt = getStoreWorkPrompt(enterpriseId, metaStaTableColumnDO.getAiCheckStdDesc(), style, metaStaTableColumnDO.getAiSceneId());
        String aiResult = aiResolve.aiResolve(enterpriseId, patrolPrompt, imageList, aiModel);
        log.info("metaColumn::{}, ai返回结果：{}", metaStaTableColumnDO.getId(), aiResult);
        return AIHelper.matchPatrolResult(aiResult, resultDOList, aiPlatform.getResultStrategy());
    }

    /**
     * 获取巡店AI评论及分数提示词
     */
    private AICommonPromptDTO getPatrolAiCommentAndScorePrompt(String standardDesc, BigDecimal score) {
        String patrolAiAnalyzedText_GetScore = redisUtilPool.hashGet("patrol_ai_prompt", "detail");
        String prompt = String.format(patrolAiAnalyzedText_GetScore, standardDesc, score);
        return AICommonPromptDTO.builder().finishPrompt(prompt).build();
    }

    /**
     * 获取图片的标准文案提示词
     */
    private AICommonPromptDTO getAiCheckStdDescPrompt(String text) {
        String aiCheckStdDesc;
        if(StringUtil.isNotBlank(text)){
            aiCheckStdDesc = String.format(redisUtilPool.getString("aiAnalyzedText_StdDesc_1"), text);
        }else{
            aiCheckStdDesc = String.format(redisUtilPool.getString("aiAnalyzedText_StdDesc_2"));
        }
        return AICommonPromptDTO.builder().finishPrompt(aiCheckStdDesc).build();
    }

    /**
     * 获取巡店/店务提示词
     */
    private AICommonPromptDTO getPatrolPrompt(String eid, String aiCheckStdDesc, String style, Long sceneId) {
        String processPromptKey = Constants.AI_OPEN_PLATFORM.PROCESS_PROMPT_KEY;
        String systemPrompt = redisUtilPool.hashGet(processPromptKey, Constants.AI_OPEN_PLATFORM.PATROL_STORE_SYSTEM_0_KEY);
        String finishPrompt = redisUtilPool.hashGet(Constants.AI_OPEN_PLATFORM.PATROL_AI_PROMPT_KEY, style);
        String prompt = String.format(finishPrompt, aiCheckStdDesc, "10");
        // 使用新的场景
        if(sceneId != null){
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            DataSourceHelper.reset();
            AiModelSceneVO aiModelScene = aiModelSceneService.detail(sceneId);
            EnterpriseModelAlgorithmDTO modelAlgorithmDTO = enterpriseModelAlgorithmService.detail(eid, sceneId);
            DataSourceHelper.changeToSpecificDataSource(dbName);
            if(aiModelScene != null){
                systemPrompt = aiModelScene.getSystemPrompt();
                String userPrompt = aiModelScene.getUserPrompt();
                if(modelAlgorithmDTO != null){
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(modelAlgorithmDTO.getUserPrompt())){
                        userPrompt = modelAlgorithmDTO.getUserPrompt();
                    }
                }

                prompt = userPrompt.replace("{{prompt}}", aiCheckStdDesc);
                if(modelAlgorithmDTO != null && StringUtil.isBlank(modelAlgorithmDTO.getSpecialPrompt())){
                    prompt = prompt + modelAlgorithmDTO.getSpecialPrompt();
                }
            }
        }
        return new AICommonPromptDTO(systemPrompt, null, prompt);
    }

    /**
     * 获取店务提示词
     * @param enterpriseId
     * @param aiCheckStdDesc
     * @return
     */
    @Override
    public AICommonPromptDTO getStoreWorkPrompt(String enterpriseId, String aiCheckStdDesc, String style, Long sceneId) {
        String processPromptKey = MessageFormat.format(Constants.STORE_WORK_AI.STORE_WORK_PROMPT_KEY, enterpriseId);
        String prompt = redisUtilPool.getString(processPromptKey);
        if(StringUtils.isNotBlank(prompt)){
            return JSONObject.parseObject(String.format(prompt, aiCheckStdDesc), AICommonPromptDTO.class);
        }
        return getPatrolPrompt(enterpriseId, aiCheckStdDesc, style, sceneId);
    }

    /**
     * 获取店报提示词
     */
    private AICommonPromptDTO getReportPrompt(String promptDimension) {
        String processPromptKey = Constants.AI_OPEN_PLATFORM.PROCESS_PROMPT_KEY;
        String systemPrompt = redisUtilPool.hashGet(processPromptKey, Constants.AI_OPEN_PLATFORM.PATROL_STORE_SYSTEM_0_KEY);
        String finalPrompt = redisUtilPool.hashGet(processPromptKey, Constants.AI_OPEN_PLATFORM.AI_REPORT_FINISH_0_KEY);
        String prompt = String.format(finalPrompt, promptDimension);
        return new AICommonPromptDTO(systemPrompt, null, prompt);
    }
}
