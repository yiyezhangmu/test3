package com.coolcollege.intelligent.util;

import com.aliyun.teaopenapi.models.Config;

public class TextUtil {
    public static com.aliyun.green20220302.Client createTextClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.setRegionId("cn-hangzhou");
        config.setEndpoint("green-cip.cn-hangzhou.aliyuncs.com");
        return new com.aliyun.green20220302.Client(config);
    }
}