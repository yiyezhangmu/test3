package com.coolcollege.intelligent.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.BATCH_ITEM_TEMPLATE;
import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.BATCH_ITEM_TEMPLATE2;

/**
 * describe: 系统日志辅助类
 *
 * @author wangff
 * @date 2025/1/21
 */
public class SysLogHelper {

    /**
     * 将预处理结果处理成extendInfo字符串
     * @param preprocessResult 预处理结果
     * @return extendInfo字符串
     */
    public static String buildExtendInfoStrByPreprocessResult(String preprocessResult) {
        String extendInfoStr = null;
        if (StringUtils.isNotBlank(preprocessResult)) {
            JSONObject extendInfo = new JSONObject();
            extendInfo.put(SysLogConstant.PREPROCESS_RESULT, preprocessResult);
            extendInfoStr = JSON.toJSONString(extendInfo);
        }
        return extendInfoStr;
    }

    /**
     * 从extendInfo获取预处理结果
     * @param extendInfoStr 扩展字段
     * @return 预处理结果
     */
    public static String getPreprocessResultByExtendInfoStr(String extendInfoStr) {
        JSONObject extendInfo = JSONObject.parseObject(extendInfoStr);
        if (Objects.nonNull(extendInfo)) {
            return extendInfo.getString(SysLogConstant.PREPROCESS_RESULT);
        }
        return null;
    }

    /**
     * 通过模板设置操作内容
     * @param template 模板
     * @param params 参数列表
     * @return 操作内容
     */
    public static String buildContent(String template, String... params) {
        if (StringUtils.isBlank(template)) {
            return null;
        }
        return MessageFormat.format(template, params);
    }

    /**
     * 获取批量操作的操作项内容
     * <p/> 例如：「样例名称1(1001)」、「样例名称2(1002)」
     * @param list 列表
     * @param nameFunc 获取名称方法
     * @param idFunc 获取id方法
     * @return 操作项内容
     */
    public static <T> String buildBatchContentItem(List<T> list, Function<T, String> nameFunc, Function<T, Object> idFunc) {
        if (CollectionUtil.isEmpty(list)) return "";
        return list.stream().filter(v -> Objects.nonNull(nameFunc.apply(v)))
                .map(v -> MessageFormat.format(BATCH_ITEM_TEMPLATE, nameFunc.apply(v), String.valueOf(idFunc.apply(v))))
                .collect(Collectors.joining("、"));
    }

    /**
     * 获取批量操作的操作项内容
     * <p/> 例如：「样例名称1」、「样例名称2」
     * @param list 列表
     * @param nameFunc 获取名称方法
     * @return 操作项内容
     */
    public static <T> String buildBatchContentItem(List<T> list, Function<T, String> nameFunc) {
        if (CollectionUtil.isEmpty(list)) return "";
        return list.stream().filter(v -> Objects.nonNull(nameFunc.apply(v)))
                .map(v -> MessageFormat.format(BATCH_ITEM_TEMPLATE2, nameFunc.apply(v)))
                .collect(Collectors.joining("、"));
    }
}
