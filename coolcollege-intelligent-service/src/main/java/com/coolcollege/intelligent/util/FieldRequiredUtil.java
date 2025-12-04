package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.common.enums.store.StoreEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.store.StoreDO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 邵凌志
 * @date 2020/7/7 18:04
 */
@Slf4j
public class FieldRequiredUtil {

    public static void check(Map<String, String> fieldMap, Object obj) {
        for(String key: fieldMap.keySet()){
            String getMethodStr = "get" + key.substring(0, 1).toUpperCase()+key.substring(1);
            try {
                Method m = obj.getClass().getMethod(getMethodStr);
                Object value =  m.invoke(obj);
                if(value == null || value.toString().trim().equals("")){
                    throw new ServiceException(StoreEnum.FIELD_NOT_NULL.getCode(), fieldMap.get(key) + "不能为空");
                }
            } catch (Exception e) {
                log.info("反射异常：" + e.getMessage());
                throw new ServiceException(StoreEnum.FIELD_NOT_NULL.getCode(), e.getMessage());
            }
        }
    }
}
