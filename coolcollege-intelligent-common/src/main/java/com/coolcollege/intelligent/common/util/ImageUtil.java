package com.coolcollege.intelligent.common.util;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil {

    public static List<String> getImageList(String img) {
        if(StringUtils.isBlank(img)){
            return null;
        }
        List<String> imageList = new ArrayList<>(Arrays.asList(img.split("https")));
        List<String> resultList = new ArrayList<>();
        imageList.forEach(s -> {
            if (StringUtils.isNotBlank(s)) {
                resultList.add("https" + s.substring(0, s.length()-1));
            }
        });
        return resultList;
    }

    public static List<String> getStoreWorkImageUrl(String imageUrls) {
        if(StringUtils.isBlank(imageUrls)){
            return Lists.newArrayList();
        }
        JSONArray jsonArray = JSONObject.parseArray(imageUrls);
        return CollStreamUtil.toList(jsonArray, v -> ((JSONObject) v).getString("handle"));
    }

    public static void main(String[] args) {
        String check = "https://oss-cool.coolstore.cn/eid/6d7c080bd55c4768807a0793fe4f3b8a/2509/bGJReq.jpg?x-oss-process=image/resize,w_2000/watermark,size_35,text_5LiH6L6-,color_FFFFFF,y_3/watermark,size_35,text_5Y2O5Y2XLTE2OTYt56aP5bu655yB5rOJ5bee5biC5Liw5rO95Yy6Lea1puilvw,color_FFFFFF,y_38/watermark,size_35,text_dW5kZWZpbmVk,color_FFFFFF,y_73/watermark,size_35,text_MjAyNS4wOS4yOC0yMjo0NzoxMw,color_FFFFFF,y_108,https://oss-cool.coolstore.cn/eid/6d7c080bd55c4768807a0793fe4f3b8a/2510/HSXvaR.jpg?x-oss-process=image/resize,w_2000/watermark,size_35,text_5rC05bK4576O6aOf6KGX,color_FFFFFF,y_3/watermark,size_35,text_5Y2O5LitLTM1Mzct5rmW5Y2X55yB5bKz6Ziz5biC5rGo572X5biCLeasouS5kA,color_FFFFFF,y_38/watermark,size_35,text_dW5kZWZpbmVk,color_FFFFFF,y_73/watermark,size_35,text_MjAyNS4xMC4xNC0xOTo1MzozMg,color_FFFFFF,y_108";
        List<String> imageList = getImageList(check);
        System.out.println();
    }

}
