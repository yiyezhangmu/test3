package com.coolcollege.intelligent.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: StringUtil
 * @Description:
 * @date 2022-08-17 11:25
 */
public class StringUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String format(String message, Map<String, String> param){
        for (String key : param.keySet()) {
            String value = param.get(key);
            if(StringUtils.isBlank(value)){
                value = "";
            }
            message = message.replace("{" + key + "}", value);
        }
        return message;
    }

    public static String formatFsCard(String message, Map<String, String> param){
        for (String key : param.keySet()) {
            String value = param.get(key);
            if(StringUtils.isBlank(value)){
                value = "";
            }
            message = message.replace("${" + key + "}", value);
        }
        return message;
    }

    /**
     * 使用逗号拼接，包含前后
     * @param list 列表
     * @return 拼接后字符串
     */
    public static String formatListComma(List<String> list) {
        return (list == null || list.isEmpty()) ? "" : "," + StringUtils.join(list, ",") + ",";
    }

    public static boolean checkIsArray(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            if (jsonNode.isArray()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean checkIsObject(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            if (jsonNode.isObject()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
