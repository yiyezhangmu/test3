package com.coolcollege.intelligent.common.enums.wechat;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangchenbiao
 * @FileName: WechatAppEnum
 * @Description:
 * @date 2023-10-17 15:08
 */
public enum WechatAppEnum {
    
    //酷店掌公众号
    TEST("wx686e6e37db5efa38","7cb77374bd194f24d5213596ec62b88b", "wx085c84fe42691bf1"),
    //四川老马扎
    SI_CHUAN_LZM("wxe8734a6cc5dafc15", "2f54c002f8b800b476611bdc7ad9afd2", "wx085c84fe42691bf1"),
    ;

    //公众号appid
    private String appId;

    //公众号appSecret
    private String appSecret;

    //小程序appid
    private String miniProgramAppId;

    WechatAppEnum(String appId, String appSecret, String miniProgramAppId) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.miniProgramAppId = miniProgramAppId;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getMiniProgramAppId() {
        return miniProgramAppId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setMiniProgramAppId(String miniProgramAppId) {
        this.miniProgramAppId = miniProgramAppId;
    }

    public static String getAppSecret(String appId){
        for (WechatAppEnum value : WechatAppEnum.values()) {
            if(value.appId.equals(appId)){
                return value.appSecret;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "WechatAppEnum{" +
                "appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", miniProgramAppId='" + miniProgramAppId + '\'' +
                '}';
    }

    public static WechatAppEnum fromJson(String json) {
        try {
            // 使用 Gson 解析 JSON 字符串
            JSONObject jsonObject = JSONObject.parseObject(json);

            String miniProgramAppId = jsonObject.getString("miniProgramAppId");
            String appId = jsonObject.getString("appId");
            String appSecret = jsonObject.getString("appSecret");

            // 查找匹配的枚举值
            for (WechatAppEnum value : WechatAppEnum.values()) {
                if (value.getMiniProgramAppId().equals(miniProgramAppId) &&
                        value.getAppId().equals(appId) &&
                        value.getAppSecret().equals(appSecret)) {
                    return value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        WechatAppEnum wechatAppEnum = fromJson("{\"miniProgramAppId\":\"wx085c84fe42691bf1\",\"appId\":\"wxe8734a6cc5dafc15\",\"appSecret\":\"2f54c002f8b800b476611bdc7ad9afd2\"}");
        System.out.println("");
    }

}
