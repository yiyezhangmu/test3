package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段相关工具类
 * @author ：xugangkun
 * @date ：2021/11/3 16:28
 */
@Slf4j
public class FieldUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 判断一个对象的选定的字段是否有值,该值不能是集合
     * @param fields
     * @param o 判断对象
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/11/3 16:33
     */
    public static Boolean isPerfect(List<String> fields, Object o) {
        //传入的对象不能是集合对象,若是，直接放回false
        if (o instanceof Map || o instanceof Collection) {
            return false;
        }
        Class beanClass = o.getClass();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            try {
                //下划线转驼峰
                String hump = lineToHump(field);
                Method getMethod = beanClass.getDeclaredMethod(getGetterName(hump), null);
                Object value  = getMethod.invoke(o, null);
                //值为null，返回false
                if (value == null) {
                    return false;
                }
                //如果值是String类型，且为空字符串，返回false
                if (value instanceof String && StringUtils.isBlank(String.valueOf(value))) {
                    return false;
                }
            } catch (Exception e) {
                log.error("FieldUtils isPerfect error", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 获得对象的字段get方法
     * @param fieldName 带下划线的字段
     * @return java.lang.String
     * @author xugangkun
     * @since 2021/11/3 16:33
     * @throws
     */
    public static String getGetterName(String fieldName) {
        return Constants.STRING_GET + toggleCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * 下划线转驼峰
     * @param str
     * @return java.lang.String
     * @author xugangkun
     * @since 2021/11/3 16:33
     * @throws
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 大小写转换
     * @param firstChar 被转换的字母
     * @return char
     * @author xugangkun
     * @since 2021/11/3 16:33
     * @throws
     */
    public static char toggleCase(char firstChar) {
        return firstChar -= 32;
    }

}
