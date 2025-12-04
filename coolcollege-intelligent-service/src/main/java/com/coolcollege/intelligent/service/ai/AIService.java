package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.model.ai.*;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.ai.vo.AIModelVO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;

import java.util.List;

/**
 * <p>
 * AI服务类
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
public interface AIService {

    /**
     * 获取AI模型列表
     * @return AI模型列表
     */
    List<AIModelVO> getAIModelList();

    /**
     * 巡店检查项AI分析及匹配结果项
     * @param enterpriseId 企业id
     * @param aiModel AI模型
     * @param imageList 图片列表
     * @param metaStaTableColumnDO 检查项
     * @param resultDOList 结果项列表
     * @param style 评语风格
     * @return AI处理结果DTO
     */
    AIResolveDTO aiPatrolResolve(String enterpriseId,
                                 AiModelLibraryDO aiModel,
                                 List<String> imageList,
                                 TbMetaStaTableColumnDO metaStaTableColumnDO,
                                 List<TbMetaColumnResultDO> resultDOList,
                                 String style);

    /**
     * AI巡检抓拍图片分析
     * @param enterpriseId 企业id
     * @param sceneId AI场景
     * @param imageList 图片列表
     * @return AI处理结果DTO
     */
    AiInspectionResult aiInspectionResolve(String enterpriseId,
                                 Long sceneId,
                                 List<String> imageList);

    /**
     * AI巡检抓拍图片分析(异步执行算法)
     * @param enterpriseId 企业id
     * @param sceneId AI场景
     * @param imageList 图片列表
     * @return AI处理结果DTO
     */
    AIResolveDTO aiAsyncInspectionResolve(String enterpriseId,
                                           Long sceneId,
                                           List<String> imageList,
                                            Long inspectionPeriodId);


    AIResultDTO aiInspectionResolveTest(String enterpriseId, Long sceneId, List<String> imageList,String userPrompt, String modelCode);

    /**
     * 巡店检查项AI分析及匹配结果项
     * @param enterpriseId 企业id
     * @param aiModel AI模型
     * @param request AI处理请求DTO
     * @return AI处理结果DTO
     */
    AIResolveDTO aiPatrolResolve(String enterpriseId, AiResolveBusinessTypeEnum businessType, AiModelLibraryDO aiModel, AIResolveRequestDTO request);

    /**
     * AI分析，不处理返回结果
     * @param enterpriseId 企业id
     * @param aiModel AI模型
     * @param promptDimension 点评维度
     * @param imageList 图片url列表
     * @return AI处理DTO
     */
    AIResultDTO aiReportResolve(String enterpriseId, AiModelLibraryDO aiModel, String promptDimension, List<String> imageList);

    /**
     * 获取图片的标准文案
     * @param enterpriseId 企业id
     * @param aiModelCode AI模型code
     * @param imageList 图片url列表
     * @param text 参考文案
     * @return 标准文案
     */
    String getAiCheckStaDesc(String enterpriseId, String aiModelCode, List<String> imageList, String text);

    /**
     * 获取AI评论及分数
     * @param enterpriseId 企业id
     * @param dto 请求DTO
     * @return 评分及评论DTO
     */
    AiCommentAndScoreVO getPatrolAiCommentAndScore(String enterpriseId, AiCommentAndScoreBatchDTO dto);


    /**
     * 店务ai
     * @param enterpriseId
     * @param aiModel
     * @param imageList
     * @param metaStaTableColumnDO
     * @param resultDOList
     * @param style
     * @return
     */
    AIResolveDTO aiStoreWork(String enterpriseId, AiModelLibraryDO aiModel, List<String> imageList, TbMetaStaTableColumnDO metaStaTableColumnDO, List<TbMetaColumnResultDO> resultDOList, String style);

    /**
     * 获取店务ai通用提示
     * @param enterpriseId
     * @param aiCheckStdDesc
     * @param style
     * @return
     */
    AICommonPromptDTO getStoreWorkPrompt(String enterpriseId, String aiCheckStdDesc, String style, Long sceneId);
}
