package com.coolcollege.intelligent.common.enums.device;

public enum DeviceAuthAppEnum {
    meituan("meituan", "9d10b933b78742b48df879b0fcfbd85c", "美团", "meituanOpenAuthService", "https://store-ossfile.oss-cn-hangzhou.aliyuncs.com/home-pic/meituan.png"),
    eleme("eleme", "a1bd578413de49d5bdec13e361dff961", "饿了么", "elemeOpenAuthService", "https://store-ossfile.oss-cn-hangzhou.aliyuncs.com/home-pic/eleme.png"),
    shian("shian", "70da6fac5d884aa1be0e37a17325888e", "食安钉", "elemeOpenAuthService", "")
    ;

    private String appId;

    private String appSecret;

    private String appName;

    private String serviceName;

    private String appIcon;
    //是否隐藏
    private boolean isHidden;

    DeviceAuthAppEnum(String appId, String appSecret, String appName, String serviceName, String appIcon) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.appName = appName;
        this.serviceName = serviceName;
        this.appIcon = appIcon;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAppName() {
        return appName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public boolean isHidden() {
        return appId.equals(shian.appId);
    }

    public static DeviceAuthAppEnum getByAppId(String appId) {
        for (DeviceAuthAppEnum value : values()) {
            if (value.appId.equals(appId)) {
                return value;
            }
        }
        return null;
    }
}
