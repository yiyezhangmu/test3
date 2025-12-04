package com.coolcollege.intelligent.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.ai.*;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * AI工具类
 * </p>
 *
 * @author wangff
 * @since 2025/7/8
 */
@Slf4j
public class AIHelper {

    /**
     * 图片预处理，限制图片大小
     *
     * @param imageList 图片url列表
     * @return 图片列表
     */
    public static List<String> imagePreprocess(List<String> imageList) {
        return imageList.stream()
                .map(url -> {
                    if (!url.contains("?x-oss-process=image/resize")) {
                        if (url.contains("?")) {
                            return url + "&x-oss-process=image/resize,w_800";
                        } else {
                            return url + "?x-oss-process=image/resize,w_800";
                        }
                    } else {
                        return url + ",w_800";
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 转化AI结果
     * @param resultStr AI结果
     * @param resultStrategy AI结果取值策略
     * @return AI结果
     */
    public static AiCommentAndScoreVO convertAiResult(String resultStr, String resultStrategy) {
        if (Constants.AI_MODEL_LIBRARY.PASS_OR_FAIL_OR_INA.equals(resultStrategy)) {
            return new AiCommentAndScoreVO(CheckResultEnum.getByCode(resultStr), null);
        } else {
            return convertAiCommentAndScoreVO(resultStr);
        }
    }

    /**
     * 从AI结果中提取巡店评价和分数
     * @param resultStr AI结果
     * @return AI结果
     */
    public static AiCommentAndScoreVO convertAiCommentAndScoreVO(String resultStr) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            if (Objects.nonNull(jsonObject)) {
                return new AiCommentAndScoreVO(resultStr, jsonObject.getBigDecimal("评估分数"));
            }
        } catch (Exception exception) {
            String resultJSON = null;
            try {
                resultJSON = resultStr.replace("```json", "").replace("```", "").trim();
                log.info("resultJSON:{}", resultJSON);
                JSONObject jsonObject = JSONObject.parseObject(resultJSON);
                if (Objects.nonNull(jsonObject)) {
                    return new AiCommentAndScoreVO(resultJSON, jsonObject.getBigDecimal("评估分数"));
                }
            } catch (Exception e) {
                return toJsonObject(resultJSON);
            }
        }
        return new AiCommentAndScoreVO(resultStr, null);
    }

    /**
     * 匹配巡店结果项
     * <p>
     *     默认AI结果为JSON格式，会校验结果的格式和分数，校验不通过抛出异常
     *     JSON格式的结果会去除JSON字符，输出纯文本
     * </p>
     * @param resultStr AI结果
     * @param resultDOList 结果项列表
     * @param resultStrategy AI结果取值策略
     * @return AI处理DTO
     */
    public static AIResolveDTO matchPatrolResult(String resultStr, List<TbMetaColumnResultDO> resultDOList, String resultStrategy) {
        if (Constants.AI_MODEL_LIBRARY.PASS_OR_FAIL_OR_INA.equals(resultStrategy)) {
            return matchColumnResultByCheckResult(new AiCommentAndScoreVO(resultStr, null), resultDOList);
        } else if(resultStr.contains("评估分数")){
            AiCommentAndScoreVO aiCommentAndScoreVO = convertAiCommentAndScoreVO(resultStr);
            // 校验AI返回的JSON格式，上一步处理了AI的结果，包括去除了换行符、\"等字符
            aiCommentAndScoreVerifyJsonAndScore(aiCommentAndScoreVO.getAiComment());
            aiCommentAndScoreVO.setAiComment(aiCommentResolve(aiCommentAndScoreVO.getAiComment()));
            return matchColumnResultByScore(aiCommentAndScoreVO, resultDOList);
        }else{
            AiCommentAndScoreVO aiCommentAndScoreVO = JSONObject.parseObject(resultStr, AiCommentAndScoreVO.class);
            return matchColumnResultByAiResult(aiCommentAndScoreVO, resultDOList);
        }
    }

    public static AiInspectionResult matchInspectionResolve(String resultStr, String resultStrategy) {
        if (Constants.AI_MODEL_LIBRARY.PASS_OR_FAIL_OR_INA.equals(resultStrategy)) {
            String checkResult = CheckResultEnum.getByCode(resultStr);
            return new AiInspectionResult(checkResult, null, resultStr);
        } else {
            if(isJsonValid(resultStr)){
                JSONObject jsonObject = JSONObject.parseObject(resultStr, JSONObject.class);
                return new AiInspectionResult(jsonObject.getString("result"), jsonObject.getString("message"), resultStr);
            }else {
                String jsonStr = extractJson(resultStr);
                JSONObject jsonObject = JSONObject.parseObject(jsonStr, JSONObject.class);
                return new AiInspectionResult(jsonObject.getString("result"), jsonObject.getString("message"), resultStr);
            }
        }
    }

    public static boolean isJsonValid(String jsonStr) {
        return JSONValidator.from(jsonStr).validate();
    }


    public static AIResolveDTO matchPatrolResult(String resultStr, List<TbMetaColumnResultDO> resultDOList) {
        try {
            if(resultStr.contains("评估分数")){
                AiCommentAndScoreVO aiCommentAndScoreVO = convertAiCommentAndScoreVO(resultStr);
                // 校验AI返回的JSON格式，上一步处理了AI的结果，包括去除了换行符、\"等字符
                aiCommentAndScoreVerifyJsonAndScore(aiCommentAndScoreVO.getAiComment());
                aiCommentAndScoreVO.setAiComment(aiCommentResolve(aiCommentAndScoreVO.getAiComment()));
                return matchColumnResultByScore(aiCommentAndScoreVO, resultDOList);
            }else{
                AiCommentAndScoreVO aiCommentAndScoreVO = JSONObject.parseObject(resultStr, AiCommentAndScoreVO.class);
                return matchColumnResultByAiResult(aiCommentAndScoreVO, resultDOList);
            }
        } catch (Exception e) {
            log.info("json格式异常:{}", resultStr);
            return null;
        }
    }

    /**
     * 根据分数匹配结果项
     * @param result AI结果
     * @param resultDOList 结果项列表
     * @return AI处理DTO
     */
    public static AIResolveDTO matchColumnResultByScore(AiCommentAndScoreVO result, List<TbMetaColumnResultDO> resultDOList) {
        AIResolveDTO aiResolveDTO = new AIResolveDTO();
        aiResolveDTO.setAiComment(result.getAiComment());
        for (TbMetaColumnResultDO columnResultDO : resultDOList) {
            JSONObject extendInfo = JSONObject.parseObject(columnResultDO.getExtendInfo());
            BigDecimal aiMaxScore = extendInfo.getBigDecimal("aiMaxScore");
            BigDecimal aiMinScore = extendInfo.getBigDecimal("aiMinScore");
            if (Objects.nonNull(aiMaxScore) && Objects.nonNull(aiMinScore)
                    && aiMinScore.compareTo(result.getAiScore()) <= 0 && aiMaxScore.compareTo(result.getAiScore()) >= 0) {
                aiResolveDTO.setColumnResult(columnResultDO);
                aiResolveDTO.setAiScore(columnResultDO.getMaxScore());
                log.info("AI分数：{}, 匹配结果项：{}", result.getAiScore(), JSONObject.toJSONString(columnResultDO));
                return aiResolveDTO;
            }
        }
        log.info("AI结果匹配失败，result:{}", JSONObject.toJSONString(result));
        throw new ServiceException(ErrorCodeEnum.AI_RESULT_MATCH_FAIL);
    }

    public static AIResolveDTO matchColumnResultByAiResult(AiCommentAndScoreVO result, List<TbMetaColumnResultDO> resultDOList) {
        AIResolveDTO aiResolveDTO = new AIResolveDTO();
        aiResolveDTO.setAiComment(result.getAiComment());
        for (TbMetaColumnResultDO columnResultDO : resultDOList) {
            if (result.getAiResult().equals(columnResultDO.getResultName())) {
                aiResolveDTO.setColumnResult(columnResultDO);
                aiResolveDTO.setAiScore(columnResultDO.getMaxScore());
                log.info("AI分数：{}, 匹配结果项：{}", aiResolveDTO.getAiScore(), JSONObject.toJSONString(columnResultDO));
                return aiResolveDTO;
            }
        }
        log.info("AI结果匹配失败，result:{}", JSONObject.toJSONString(result));
        throw new ServiceException(ErrorCodeEnum.AI_RESULT_MATCH_FAIL);
    }

    /**
     * 根据检查项结果映射匹配结果项
     * @param result AI结果
     * @param resultDOList 结果项列表
     * @return AI处理DTO
     */
    public static AIResolveDTO matchColumnResultByCheckResult(AiCommentAndScoreVO result, List<TbMetaColumnResultDO> resultDOList) {
        AIResolveDTO aiResolveDTO = new AIResolveDTO();
        aiResolveDTO.setAiComment(CheckResultEnum.getByCode(result.getAiComment()));
        for (TbMetaColumnResultDO columnResultDO : resultDOList) {
            if (columnResultDO.getMappingResult().equals(result.getAiComment())) {
                aiResolveDTO.setColumnResult(columnResultDO);
                aiResolveDTO.setAiScore(columnResultDO.getMaxScore());
                log.info("AI分数：{}, 匹配结果项：{}", result.getAiScore(), JSONObject.toJSONString(columnResultDO));
                return aiResolveDTO;
            }
        }
        log.info("AI结果匹配失败，result:{}", JSONObject.toJSONString(result));
        throw new ServiceException(ErrorCodeEnum.AI_RESULT_MATCH_FAIL);
    }

    /**
     * 匹配检查结果项
     * <p> 只要有一个不合格，结果就是不合格、只有全部都不适用，结果才是不适用、部分合格和部分不适用，结果为合格
     * @param resultMappingList 结果项列表
     * @return PASS/FAIL/INAPPLICABLE
     */
    public static String matchCheckResult(List<String> resultMappingList) {
        List<String> failList = new ArrayList<>();
        List<String> passList = new ArrayList<>();
        for (String resultMapping : resultMappingList) {
            if (CheckResultEnum.FAIL.getCode().equals(resultMapping)) {
                failList.add(resultMapping);
            } else if (CheckResultEnum.PASS.getCode().equals(resultMapping)) {
                passList.add(resultMapping);
            }
        }
        if (CollectionUtils.isNotEmpty(failList)) {
            return CheckResultEnum.FAIL.getCode();
        } else if (CollectionUtils.isNotEmpty(passList)) {
            return CheckResultEnum.PASS.getCode();
        } else {
            return CheckResultEnum.INAPPLICABLE.getCode();
        }
    }

    /**
     * AI结果校验JSON和分数
     * @param aiResult AI结果
     */
    public static void aiCommentAndScoreVerifyJsonAndScore(String aiResult) {
        if (!verifyAIResultJson(aiResult)) {
            log.error("AI结果json格式错误:{}", JSONObject.toJSONString(aiResult));
            throw new ServiceException(ErrorCodeEnum.AI_RESULT_JSON_ERROR);
        }
        if (Objects.isNull(aiResult)) {
            log.error("AI结果分数为空:{}", JSONObject.toJSONString(aiResult));
            throw new ServiceException(ErrorCodeEnum.AI_RESULT_SCORE_EMPTY);
        }
    }

    /**
     * 校验AI结果的json格式是否正确
     */
    private static Boolean verifyAIResultJson(String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            return Objects.nonNull(jsonObject);
        } catch (Exception e) {
            return false;
        }
    }

    private static AiCommentAndScoreVO toJsonObject(String text) {
        String jsonStr = extractJson(text);
        if (StringUtil.isBlank(jsonStr)) {
            return new AiCommentAndScoreVO(text, null);
        }
        try {
            // 解析原始JSON
            JSONObject original = JSONObject.parseObject(jsonStr.replaceAll("\n", ""));
            BigDecimal aiScore = original.getBigDecimal("评估分数");
            original.remove("评估分数");
            return new AiCommentAndScoreVO(JSONObject.toJSONString(original), aiScore);
        } catch (Exception e) {
            log.info("JSON处理错误: " + e.getMessage());
        }
        return new AiCommentAndScoreVO(text, null);
    }

    /**
     * 从字符串中提取JSON部分
     */
    private static String extractJson(String input) {
        int start = input.indexOf("{");
        int end = input.lastIndexOf("}");
        return (start != -1 && end > start) ? input.substring(start, end + 1) : null;
    }

    /**
     * 以OpenAI的形式调用RestApi接口
     * @param url 地址
     * @param model 模型名称
     * @param apiKey apiKey
     * @param aiCommonPromptDTO AI调用通用提示词DTO
     * @param imageList 图片列表
     * @return AI结果
     */
    public static String openAIRestApiExecute(String url, String model, String apiKey, AICommonPromptDTO aiCommonPromptDTO, List<String> imageList) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new ServiceException(ErrorCodeEnum.AI_PICTURE_EMPTY);
        }
        OpenAIRestAPIDTO openAIRestAPIDTO = OpenAIRestAPIDTO.create(model, aiCommonPromptDTO);
        try {
            // 传输图片
            // 目前接入的平台不限制图片张数，但是图片数量受总token数限制
            log.info("AI生成中");
            openAIRestAPIDTO.addUserMessage(aiCommonPromptDTO.getFinishPrompt(), imageList);
            openAIRestAPIDTO.setTemperature(0.2f);
            return restApiExecute(url, apiKey, openAIRestAPIDTO);
        } catch (Exception e) {
            log.error("AI算法调用失败", e);
            throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
        }
    }

    private static String restApiExecute(String url, String apiKey, OpenAIRestAPIDTO openAIRestAPIDTO) {
        String bodyStr = JSONObject.toJSONString(openAIRestAPIDTO);
        HttpResponse httpResponse = HttpUtil.createRequest(Method.POST, url)
                .auth("Bearer " + apiKey)
                .body(bodyStr)
                .execute();
        log.info("restApiExecute-body:{}", bodyStr);
        if (Objects.nonNull(httpResponse) && httpResponse.isOk()) {
            JSONObject body = JSONObject.parseObject(httpResponse.body());
            if (Objects.nonNull(body)) {
                return body.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            }
        }
        log.info("AI算法调用失败, response:{}, request:{}", JSONObject.toJSONString(httpResponse), JSONObject.toJSONString(openAIRestAPIDTO));
        throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
    }

    /**
     * 将json格式的AI评价转换成纯字符串
     */
    public static String aiCommentResolve(String comment) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(comment);
            StringBuilder sb = new StringBuilder();
            formatJson(node, sb, 0);
            return sb.toString();
        } catch (Exception e) {
            log.error("AI评价格式转换失败", e);
        }
        return comment;
    }

    private static void formatJson(JsonNode node, StringBuilder sb, int indent) {
        if (indent > 3) {
            throw new RuntimeException("json深度过大");
        }
        node.fields().forEachRemaining(o -> {
            String key = o.getKey();
            JsonNode value = o.getValue();
            if (key.equals("评估分数")) return;
            if (value.isObject()) {
                appendLine(sb, indent, key + ": ");
                value.fields().forEachRemaining(field -> formatJson(field.getValue(), sb, indent + 1));
                formatJson(value, sb, indent + 1);
            } else if (value.isArray()) {
                appendLine(sb, indent, key + ": ");
                value.forEach(element -> formatJson(element, sb, indent + 1));
            } else {
                String valueStr = value.isTextual() ? value.asText() : value.toString();
                appendLine(sb, indent, key + ": " + valueStr);
            }
        });
    }

    private static void appendLine(StringBuilder sb, int indent, String content) {
        while (indent-- > 0) sb.append("\t");
        sb.append(content.replace("\"", "")).append("\n");
    }

    /**
     * 从字符串中提取列表JSON部分
     */
    public static String extractArrJson(String input) {
        int start = input.indexOf("[");
        int end = input.lastIndexOf("]");
        return (start != -1 && end > start) ? input.substring(start, end + 1) : null;
    }
}
