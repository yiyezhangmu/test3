package com.coolcollege.intelligent.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/12
 */
public  class FormDataUtil {


    public  static JSONArray parseArrayData(String key, String inKey, String inKey2, JSONObject data) {
        JSONArray jsonArray = data.getJSONArray(key);
        if(CollectionUtils.isEmpty(jsonArray)){
            return new JSONArray();
        }
        List<String> selectedValueList = ListUtils.emptyIfNull(jsonArray)
                .stream()
                .map(s -> {
                    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(s);
                    JSONObject inKyejsonObject = jsonObject.getJSONObject(inKey);
                    if(Objects.isNull(inKyejsonObject)){
                        return null;
                    }
                    return inKyejsonObject.getString(inKey2);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return JSONArray.parseArray(JSON.toJSONString(selectedValueList));

    }
    public static String parseStringData(String key, String inKey, String inKey2, JSONObject data) {

        JSONObject jsonObject = data.getJSONObject(key);
        if(Objects.isNull(jsonObject)){
            return null;
        }
        JSONObject inKeyJsonObject = jsonObject.getJSONObject(inKey);
        if(Objects.isNull(inKeyJsonObject)){
            return null;
        }
        return inKeyJsonObject.getString(inKey2);
    }


}
