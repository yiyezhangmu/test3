package com.coolcollege.intelligent.service.aliyun;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;

/**
 * @author zhangchenbiao
 * @FileName: AliyunSmsService
 * @Description: 阿里云短信
 * @date 2021-07-23 11:33
 */
public interface AliyunSmsService {

    /**
     * 发送短信
     * @param mobile
     * @param codeType
     * @return
     */
    ResponseResult sendSmsCode(String mobile, SmsCodeTypeEnum codeType, String appType);
}
