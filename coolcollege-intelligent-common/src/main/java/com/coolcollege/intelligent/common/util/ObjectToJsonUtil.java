package com.coolcollege.intelligent.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class ObjectToJsonUtil {

    /**
     * 将对象转换为用于签名的JSON字符串
     *
     * @param target 要转换的对象
     * @return JSON字符串
     */
    public static String toJsonForSignature(Object target) {
        if (target == null) {
            return null;
        }
        try {
            ObjectMapper signatureMapper = new ObjectMapper();
            // 忽略null值
            signatureMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // 按字典序排序
            signatureMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            // 对所有属性按字母顺序排序
            signatureMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            // 其他必要配置
            signatureMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            signatureMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return signatureMapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON conversion error", e);
        }
    }

}
