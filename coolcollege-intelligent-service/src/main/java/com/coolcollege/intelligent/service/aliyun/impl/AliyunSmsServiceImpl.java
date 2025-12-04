package com.coolcollege.intelligent.service.aliyun.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.SmsCodeUtil;
import com.coolcollege.intelligent.model.aliyun.response.AliyunSmsResponse;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import com.coolcollege.intelligent.service.aliyun.AliyunSmsService;
import com.coolcollege.intelligent.util.AliyunUtilSmsKdz;
import com.coolcollege.intelligent.util.MobileUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangchenbiao
 * @FileName: AliyunSmsServiceImpl
 * @Description: 发送短信
 * @date 2021-07-23 11:37
 */
@Service
@Slf4j
public class AliyunSmsServiceImpl implements AliyunSmsService {

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.domain}")
    private String smsDomain;

    @Value("${aliyun.sms.regionId}")
    private String regionId;

    @Resource
    private RedisUtilPool redisUtilPool;

    private String getAccessKeyId(String appType) {
        return accessKeyId;
    }

    private String getAccessKeySecret(String appType) {
        return accessKeySecret;
    }

    @Override
    public ResponseResult sendSmsCode(String mobile, SmsCodeTypeEnum codeType, String appType) {
        String smsCodeKey = codeType + ":" + mobile;
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(smsDomain);
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        String smsCode = SmsCodeUtil.getRandNum();
        String signName = redisUtilPool.getString("sms_sign_name");
        String templateCode = MobileUtil.getSmsTemplateCode(mobile);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("code", SmsCodeUtil.getRandNum());
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("PhoneNumbers", MobileUtil.transNoPlusAndBlank(mobile));
        request.putQueryParameter("SignName", StringUtils.isBlank(signName) ? "杭州好多店智能科技" : signName);
        JSONObject templateParamMap = new JSONObject();
        templateParamMap.put("code", smsCode);
        request.putQueryParameter("TemplateParam", templateParamMap.toJSONString());
        log.info("sendSmsCode templateCode:{}, AccessKeyId:{}, AccessKeySecret:{}", templateCode, getAccessKeyId(appType), getAccessKeySecret(appType));
        String result = AliyunUtilSmsKdz.handleCommonRequest(request, getAccessKeyId(appType), getAccessKeySecret(appType));
        AliyunSmsResponse resultResponse = JSONObject.parseObject(result, AliyunSmsResponse.class);
        if(AliyunSmsResponse.CODE.equals(resultResponse.getCode())){
            redisUtilPool.setString(smsCodeKey, smsCode, codeType.getCacheSeconds());
            return ResponseResult.success(true);
        }
        return ResponseResult.fail(ErrorCodeEnum.SEND_CODE_ERROR);
    }
}
