package com.coolcollege.intelligent.service.ai.impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreBatchDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.service.ai.AIOpenService;
import com.coolcollege.intelligent.service.ai.DashScopeService;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 阿里百炼大模型 AI服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
@Service("bailianAIOpenServiceImpl")
@Slf4j
public class BailianAIOpenServiceImpl implements AIOpenService {
    @Resource
    private DashScopeService dashScopeService;
    @Resource
    private RedisUtilPool redisUtilPool;

    private static final String defaultApiKey = "sk-a6146dcdd6d84ea8b64245ddcb7cc396";


    @Override
    public String aiResolve(String enterpriseId, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList, AiModelLibraryDO aiModel) {
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        String apiKey = redisUtilPool.hashGet("ai_analysis_enterprise_key", enterpriseId, defaultApiKey);
        return AIHelper.openAIRestApiExecute(url, aiModel.getCode(), apiKey, aiCommonPromptDTO, imageList);
    }

//    @Override
    @Deprecated
    public AIResolveDTO aiResolve(String enterpriseId,
                                  List<String> imageList,
                                  TbMetaStaTableColumnDO metaStaTableColumnDO,
                                  List<TbMetaColumnResultDO> resultDOList,
                                  String style) {

        if (CollectionUtils.isEmpty(imageList)) {
            return new AIResolveDTO(BigDecimal.ZERO);
        }
        // 预处理 imageList，确保每个 URL 包含 ?x-oss-process=image/resize,w_800
        List<String> processedImageList = AIHelper.imagePreprocess(imageList);
        AiCommentAndScoreBatchDTO batchDTO = new AiCommentAndScoreBatchDTO(processedImageList, metaStaTableColumnDO.getAiCheckStdDesc(), BigDecimal.valueOf(10));
        AiCommentAndScoreVO result;
        try {
            result = dashScopeService.getPatrolAiCommentAndScore(enterpriseId, batchDTO, style);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("ai算法调用失败", e);
            throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
        }
        // AI结果校验JSON格式和分数
        AIHelper.aiCommentAndScoreVerifyJsonAndScore(result.getAiComment());
        return AIHelper.matchColumnResultByScore(result, resultDOList);
    }
}
