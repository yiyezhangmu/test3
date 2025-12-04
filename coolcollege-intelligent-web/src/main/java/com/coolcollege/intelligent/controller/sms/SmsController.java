package com.coolcollege.intelligent.controller.sms;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.http.HttpHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.SmsCodeUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.model.sms.SendSmsCodeDTO;
import com.coolcollege.intelligent.service.aliyun.AliyunSmsService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * @author zhangchenbiao
 * @FileName: SmsController
 * @Description: 短信验证码
 * @date 2021-07-21 11:17
 */
@RestController
@ErrorHelper
@Slf4j
@RequestMapping("/v3/sms")
public class SmsController {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private AliyunSmsService aliyunSmsService;

    private final int SEND_SMS_LIMIT_COUNT = 10;

    private final int SEND_SMS_IP_LIMIT_COUNT = 100;

    @PostMapping("/sendSmsCode")
    public ResponseResult sendSmsCode(@RequestParam(value = "appType", required = false) String appType,
                                      @Validated @RequestBody SendSmsCodeDTO param, HttpServletRequest request){
        //一天同一个手机号限制发送10条短信
        String codeKey = "sendSmsCode_" + LocalDate.now() + ":" + param.getMobile();
        Long sendCount = redisUtilPool.incrby(codeKey, 1, 24 * 60 * 60);
        if(sendCount > SEND_SMS_LIMIT_COUNT){
            return ResponseResult.fail(ErrorCodeEnum.SEND_SMS_LIMIT_COUNT);
        }
        String ip = HttpHelper.getIpAddr(request);
        String ipCacheKey = "sendSmsCode_IP_" + LocalDate.now() + ":" + ip;
        Long ipSendCount = redisUtilPool.incrby(ipCacheKey, 1, 24 * 60 * 60);
        if(ipSendCount > SEND_SMS_IP_LIMIT_COUNT){
            return ResponseResult.fail(ErrorCodeEnum.SEND_SMS_LIMIT_COUNT);
        }
        return aliyunSmsService.sendSmsCode(param.getMobile(), param.getCodeType(), appType);
    }

    @PostMapping("/sendSmsCode/test")
    public ResponseResult sendSmsCodeTest(@Validated @RequestBody SendSmsCodeDTO param){
        String smsCode = SmsCodeUtil.getRandNum();
        redisUtilPool.setString(param.getCodeType() + ":" + param.getMobile(), smsCode, param.getCodeType().getCacheSeconds());
        return ResponseResult.success(smsCode);
    }

    @GetMapping("/test")
    public ResponseResult test() throws InterruptedException {
        String uuid = UUIDUtils.get8UUID();
        Boolean lock = redisUtilPool.lock("00000", uuid);
        if(lock){
            log.info("获取锁成功>>>>>>>>>》》》》》》》》》");
            Thread.sleep(3000);
        }else{
            log.info("##########获取锁失败");
        }
        return ResponseResult.success(redisUtilPool.unlock("00000", uuid));
    }

}
