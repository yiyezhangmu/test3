package com.coolcollege.intelligent.service.ai;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreBatchDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.util.AIHelper;
import com.coolcollege.intelligent.util.RedisUtilPool;
import io.reactivex.Flowable;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: DashscopeService
 * @Description:
 * @date 2025-01-16 11:05
 */
@Slf4j
@Service
public class DashScopeService {

    @Resource
    private RedisUtilPool redisUtilPool;

    private static final String defaultApiKey = "sk-a6146dcdd6d84ea8b64245ddcb7cc396";

    /**
     * 获取图片的标准文案
     * @param enterpriseId
     * @param imgUrlList
     * @param text
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public String getAiCheckStdDesc(String enterpriseId, List<String> imgUrlList, String text) throws ApiException, NoApiKeyException, UploadFileException {
        String aiCheckStdDesc = null;
        if(StringUtil.isNotBlank(text)){
            aiCheckStdDesc = String.format(redisUtilPool.getString("aiAnalyzedText_StdDesc_1"), text);
        }else{
            aiCheckStdDesc = String.format(redisUtilPool.getString("aiAnalyzedText_StdDesc_2"));
        }
        String modelName = redisUtilPool.getString("ai_analysis_model_name");
        String apiKey = redisUtilPool.hashGet("ai_analysis_enterprise_key", enterpriseId, defaultApiKey);
        log.info("aiCheckStdDesc:{}", aiCheckStdDesc);
        List<Map<String, Object>> content = CollStreamUtil.toList(imgUrlList, v -> Collections.singletonMap("image", v));
        content.add(Collections.singletonMap("text", aiCheckStdDesc));
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue()).content(content).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(modelName)
                .apiKey(apiKey)
                .message(userMessage)
                .build();
        String resultStr = (String) conv.call(param).getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        String resultJSON = extractArrJson(resultStr);
        try {
            JSONArray jsonArray = JSONObject.parseArray(resultJSON);
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
            log.info("resultStr:{}", resultStr);
            return resultStr;
        }
        return resultStr;
    }

    /**
     * 获取ai评论及分数
     * @param enterpriseId
     * @param request
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public AiCommentAndScoreVO getAiCommentAndScore(String enterpriseId, AiCommentAndScoreDTO request) throws ApiException, NoApiKeyException, UploadFileException {
        String aiAnalyzedText_GetScore = redisUtilPool.getString("aiAnalyzedText_GetScore");
        String aiText = String.format(aiAnalyzedText_GetScore, request.getStandardDesc(), request.getScore());
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", request.getImageUrl()),
                        Collections.singletonMap("text", aiText))).build();
        return aiCommentAndScore(enterpriseId, userMessage);
    }

    /**
     * 巡店获取ai评论及分数
     * @param enterpriseId 企业id
     * @param request 请求dto
     */
    public AiCommentAndScoreVO getPatrolAiCommentAndScore(String enterpriseId, AiCommentAndScoreBatchDTO request, String style) throws NoApiKeyException, UploadFileException {
        if (CollectionUtils.isEmpty(request.getImageList())) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        String patrolAiAnalyzedText_GetScore;
        if (StringUtils.isNotBlank(style)) {
            patrolAiAnalyzedText_GetScore = redisUtilPool.hashGet("patrol_ai_prompt", style);
        } else {
            patrolAiAnalyzedText_GetScore = redisUtilPool.getString("patrolAiAnalyzedText_GetScore");
        }
        String aiText = String.format(patrolAiAnalyzedText_GetScore, request.getStandardDesc(), request.getScore());
        List<Map<String, Object>> content = CollStreamUtil.toList(request.getImageList(), v -> Collections.singletonMap("image", v));
        content.add(Collections.singletonMap("text", aiText));
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue()).content(content).build();
        return aiCommentAndScore(enterpriseId, userMessage);
    }

    private AiCommentAndScoreVO aiCommentAndScore(String enterpriseId, MultiModalMessage userMessage) throws NoApiKeyException, UploadFileException {
        long startTime = System.currentTimeMillis();
        String modelName = redisUtilPool.getString("ai_analysis_model_name");
        String apiKey = redisUtilPool.hashGet("ai_analysis_enterprise_key", enterpriseId, defaultApiKey);
        log.info("modelName:{}, apiKey:{}", modelName, apiKey);
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(modelName)
                .apiKey(apiKey)
                .message(userMessage)
                .build();
        MultiModalConversationResult result = conv.call(param);
        log.info("ai 模型分析 结果:{}", result);
        String resultStr = (String)result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        log.info("ai 模型分析 耗时:{}", (System.currentTimeMillis() - startTime)/1000);
        return AIHelper.convertAiCommentAndScoreVO(resultStr);
    }

    /**
     * 从字符串中提取列表JSON部分
     */
    private static String extractArrJson(String input) {
        int start = input.indexOf("[");
        int end = input.lastIndexOf("]");
        return (start != -1 && end > start) ? input.substring(start, end + 1) : null;
    }

    public static AiCommentAndScoreVO streamCallWithMessage(MultiModalMessage Msg) throws NoApiKeyException, ApiException, InputRequiredException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        StringBuilder resultContent = new StringBuilder();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("sk-a6146dcdd6d84ea8b64245ddcb7cc396")
                // 此处以 qvq-max 为例，可按需更换模型名称
                .model("qvq-max")
                .messages(Arrays.asList(Msg))
                .incrementalOutput(true)
                .build();;
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(message -> {
            List<Map<String, Object>> content = message.getOutput().getChoices().get(0).getMessage().getContent();
            if (Objects.nonNull(content) && !content.isEmpty()) {
                resultContent.append(content.get(0).get("text"));
            }
        });
        String resultStr = resultContent.toString();
        return AIHelper.convertAiCommentAndScoreVO(resultStr);
    }
}
