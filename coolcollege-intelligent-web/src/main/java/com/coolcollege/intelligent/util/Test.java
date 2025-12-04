package com.coolcollege.intelligent.util;

import cn.hutool.core.map.MapUtil;
import com.coolcollege.intelligent.common.util.MD5Util;

import java.util.HashMap;
import java.util.Map;

public class Test {

   public static void main1(String[] args) {
       Map<String, String> json = new HashMap<>();
       json.put("_aid","DC-000392");
       json.put("_akey","a6e1e81ae597fb575922b030e0eb0dae");
       json.put("_mt","open.shopweb.security.mobileLogin");
       json.put("_sm","md5");
       json.put("_requestMode","POST");
       json.put("_version","v1");
       json.put("_timestamp","20241015101209");
       json.put("userName","15611678868");
       json.put("password","Aa931016");
       String s = MapUtil.sortJoin(json, "", "", true);
       String sign = MD5Util.md5("c51d8b662821ed6798135e15e5b181e4"+s+"c51d8b662821ed6798135e15e5b181e4").toUpperCase();
       System.out.println(s);
       System.out.println(sign);

   }

    public static void main(String[] args) {
        Map<String, String> json = new HashMap<>();
        json.put("_aid","DC-000392");
        json.put("_akey","a6e1e81ae597fb575922b030e0eb0dae");
        json.put("_mt","open.shopweb.device.getVideoDeviceList");
        json.put("_sm","md5");
        json.put("_requestMode","POST");
        json.put("_version","v1");
        json.put("_timestamp","20241015101209");
        json.put("id","263851");
        String s = MapUtil.sortJoin(json, "", "", true);
        String sign = MD5Util.md5("c51d8b662821ed6798135e15e5b181e4"+s+"c51d8b662821ed6798135e15e5b181e4").toUpperCase();
        System.out.println(s);
        System.out.println(sign);

    }


}

