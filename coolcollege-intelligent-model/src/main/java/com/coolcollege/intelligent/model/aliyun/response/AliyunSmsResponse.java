package com.coolcollege.intelligent.model.aliyun.response;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: AliyunSmsResponse
 * @Description: 阿里云发送短信response
 * @date 2021-07-23 13:53
 */
@Data
public class AliyunSmsResponse {

    public final static String CODE = "OK";

    /*{
        "Message": "OK",
            "RequestId": "873043ac-bcda-44db-9052-2e204c6ed20f",
            "BizId": "607300000000000000^0",
            "Code": "OK"
    }*/

    private String Message;

    private String RequestId;

    private String BizId;

    private String Code;
}
