package com.coolcollege.intelligent.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: UrlUtil
 * @Description:
 * @date 2022-08-26 15:06
 */
public class UrlUtil {

    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    public static JSONObject getUrlJSONObject(String param) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(param)) {
            return jsonObject;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                jsonObject.put(p[0], p[1]);
            }
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        String url = "questionOrder&eid=45f92210375346858b6b6694967f44de&corpId=dingef2502a50df74ccc35c2f4657eb6378f&appType=dingding&questionParentInfoId=26645&currTime=1661496970444";
        JSONObject urlParams = getUrlJSONObject(url);
        System.out.println(JSONObject.toJSONString(url));
    }

}
