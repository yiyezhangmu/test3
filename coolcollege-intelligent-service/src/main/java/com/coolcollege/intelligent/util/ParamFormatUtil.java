package com.coolcollege.intelligent.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ParamFormatUtil {


    /**
     * 嵌套对象驼峰 下划线互相转换
     * @Description 嵌套对象驼峰 下划线互相转换
     * @param data
     * @param type
     * @return list
     * @throws Exception
     */
    public static Object format(Object data, String type) {
        if (data instanceof List) {
            return formatList(data, type);
        } else {
            return formatObject(data, type);
        }
    }


    /**
     * 数组转换
     * @Description 数组转换
     * @param data
     * @param type
     * @return list
     * @throws Exception
     */
    private static Object formatList(Object data, String type) {
        List<Object> nlist = new ArrayList<Object>();
        for (Object dataChild : (List) data) {
            Object dataNew = formatObject(dataChild, type);
            if (dataNew != null) {
                nlist.add(dataNew);
            }
        }
        return nlist;
    }


    /**
     * 数组转换
     * @Description 数组转换
     * @param data
     * @param type
     * @return list
     * @throws Exception
     */
    private static Object formatObject(Object data, String type) {
        if (data instanceof List) {
            return formatList(data, type);
        }
        JSONObject json;
        try {
            json = (JSONObject) JSONObject.toJSON(data);
        } catch (Exception e) {
            return data;
        }
        JSONObject jsonNew = new JSONObject();
        if (json == null) {
            return null;
        }
        Set<String> jsonKeys = json.keySet();
        Iterator<String> it = jsonKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String newKey = "";
            if ("1".equals(type)) {
                newKey = HumpToUnderline(key);
            } else if("2".equals(type)){
                newKey = UnderlineToHump(key);
            }else{
                return null;
            }
            if (StringUtils.isEmpty(json.get(key))) {
                jsonNew.put(newKey, json.get(key));
                continue;
            }
            if (json.get(key) instanceof List) {
                Object o =  formatList(json.get(key),type);
                jsonNew.put(newKey, o);
            } else {
                Object o = formatObject(json.get(key),type);
                if (o instanceof Boolean) {
                    o = o.toString();
                }
                jsonNew.put(newKey, o);
            }
        }
        return jsonNew;
    }

    /**
     * 字符串下划线转换驼峰
     * @Description 字符串下划线转换驼峰
     * @param para
     * @return list
     * @throws Exception
     */
    public static String UnderlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split("_");
        for (String s : a) {
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();

    }

    /**
     * 字符串下划线转大驼峰
     * @Description 字符串下划线转大驼峰
     * @param para
     * @return list
     * @throws Exception
     */
    public static String UnderlineToBigHump(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split("_");
        for (String s : a) {
            result.append(s.substring(0, 1).toUpperCase());
            result.append(s.substring(1).toLowerCase());
        }
        return result.toString();

    }

    /**
     * 字符串驼峰转换下划线
     * @Description 字符串驼峰转换下划线
     * @param para
     * @return list
     * @throws Exception
     */
    public static String HumpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }


}
