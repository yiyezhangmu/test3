package com.coolcollege.intelligent.common.util;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.LogField;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: huhu
 * @Date: 2025/1/21 14:31
 * @Description:
 */
@Slf4j
public class LogUtil {

    /**
     * 记录每个修改字段的分隔符
     */
    public static final String SEPARATOR = "\n";

    public static <T> T paresString(String param, String fieldName, Class<T> clazz) {
        JSONObject paramJson = JSONObject.parseObject(param);
        if (Objects.nonNull(paramJson)) {
            return JSONObject.parseObject(paramJson.getString(fieldName), clazz);
        }
        return null;
    }

    /**
     * 比较两个对象,并返回不一致的信息
     *
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    public static String compareTwoObj(Object oldObj, Object newObj) {

        String str = "";
        //获取对象的class
        Class<?> oldClass = oldObj.getClass();
        Class<?> newClass = newObj.getClass();
        //获取对象的属性列表
        Field[] oldFields = oldClass.getDeclaredFields();
        Field[] newFields = newClass.getDeclaredFields();

        for (int i = 0; i < oldFields.length; i++) {
            if ("serialVersionUID".equals(oldFields[i].getName())) {
                continue;
            }
            oldFields[i].setAccessible(true);
            newFields[i].setAccessible(true);

            // 这样就获取到这个注解属性了
            LogField fieldChinese = oldFields[i].getAnnotation(LogField.class);
            //无对应注解则说明该字段无需比较
            if (fieldChinese == null || StringUtils.isBlank(fieldChinese.name())) {
                continue;
            }
            //获取注解中字段名
            String fieldName = fieldChinese.name();

            try {
                PropertyDescriptor oldPd = new PropertyDescriptor(oldFields[i].getName(), oldClass);
                PropertyDescriptor newPd = new PropertyDescriptor(newFields[i].getName(), newClass);
                Method oldReadMethod = oldPd.getReadMethod();
                Method newReadMethod = newPd.getReadMethod();
                // 获取对应字段的值
                Object oldValue = valueTrans(oldReadMethod.invoke(oldObj), fieldName);
                Object newValue = valueTrans(newReadMethod.invoke(newObj), fieldName);
                // 获取差异字段
                str = getDifferenceFieldStr(str, i, fieldName, oldValue, newValue);
            } catch (Exception e) {
                log.error("记录对象更新失败", e);
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
            }
        }
        return str;
    }

    /**
     * 字段值转换，用于处理特殊字段
     * @param value 字段值
     * @param fieldName 字段名
     * @return 字段值
     */
    private static Object valueTrans(Object value, String fieldName) {
        try {
            // 营业时间这里前端传的规则是 小时数*3600000+分钟数*60000+秒数*1000，因为每次的秒数不同，导致前端未修改营业时间，但是传到后端的值不一致，所以这里对时分进行处理
            if ("营业时间".equals(fieldName) && null != value && StringUtils.isNotBlank(value.toString())) {
                String[] businessHours = value.toString().split(",");
                return Arrays.stream(businessHours).map(v -> {
                    int hour = Integer.parseInt(v) / 3600000;
                    int minute = (Integer.parseInt(v) % 3600000) / 60000;
                    return String.format("%02d:%02d", hour, minute);
                }).collect(Collectors.joining("-"));
            } else {
                return value;
            }
        } catch (Exception e) {
            log.error("字段值转换失败", e);
            return value;
        }
    }

    /**
     * 获取差异字段新旧值
     *
     * @param str
     * @param i
     * @param fieldName
     * @param oldValue
     * @param newValue
     * @return
     */
    private static String getDifferenceFieldStr(String str, int i, String fieldName, Object oldValue, Object newValue) {

        if (null == oldValue || StringUtils.isBlank(oldValue.toString())) {
            oldValue = "无";
        }
        if (null == newValue || StringUtils.isBlank(newValue.toString())) {
            newValue = "无";
        }
        if (!oldValue.equals(newValue)) {
            if (i != 0) {
                str += SEPARATOR;
            }
            str += fieldName + ":" + "将" + oldValue + "修改为" + newValue;
        }
        return str;
    }
}
