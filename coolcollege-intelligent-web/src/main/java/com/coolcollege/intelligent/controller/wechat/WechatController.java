package com.coolcollege.intelligent.controller.wechat;

import cn.hutool.crypto.digest.DigestUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import com.coolcollege.intelligent.model.wechat.request.WechatLoginRequest;
import com.coolcollege.intelligent.model.wechat.request.WechatMessageRequest;
import com.coolcollege.intelligent.model.wechat.request.WechatSignRequest;
import com.coolcollege.intelligent.model.wechat.vo.WechatLoginUserInfoVO;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;

/**
 * @author zhangchenbiao
 * @FileName: WechatController
 * @Description:微信接口
 * @date 2023-10-17 14:49
 */
@Slf4j
@Api(tags = "微信相关")
@RestController
@RequestMapping("/wechat")
public class WechatController {

    @Resource
    private WechatService wechatService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @PostMapping("/getSignature")
    public ResponseResult getSignature(@Valid @RequestBody WechatSignRequest param){
        return ResponseResult.success(wechatService.getSignature(param.getAppId(), param.getUrl()));
    }

    @GetMapping("/check")
    public String checkFromWeChat(@RequestParam("signature") String signature,
                                  @RequestParam("timestamp") Long timestamp,
                                  @RequestParam("nonce") String nonce,
                                  @RequestParam("echostr") String echostr) {
        log.info("signature:{}, timestamp:{}, nonce:{}, echostr:{}", signature, timestamp, nonce, echostr);
        if (handleWechatOperation(signature, timestamp, nonce)) {
            return echostr;
        }
        throw new ServiceException(ErrorCodeEnum.SIGN_FAIL);
    }

    @ApiOperation("通过授权code获取accessToken及openId")
    @GetMapping("/getWechatAccessToken")
    public ResponseResult<WechatLoginUserInfoVO> getWechatAccessToken(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("appId") String appId, @RequestParam("code") String code){
        ValidateUtil.validateString(appId, code);
        return ResponseResult.success(wechatService.getUserInfo(enterpriseId, appId, code));
    }

    @ApiOperation("通过openId和手机号验证码登录")
    @PostMapping("/enterprises/getLoginAccessToken")
    public ResponseResult<WechatLoginUserInfoVO> getLoginAccessToken(@RequestParam("enterpriseId") String enterpriseId, @Validated @RequestBody WechatLoginRequest param){
        String smsCodeKey = SmsCodeTypeEnum.LOGIN + ":"+ param.getMobile();
        if(StringUtils.isNotBlank(param.getSmsCode())){
            String code = redisUtilPool.getString(smsCodeKey);
            if(StringUtils.isBlank(code)){
                return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
            }
            if(!code.equals(param.getSmsCode())){
                return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
            }
        }
        return ResponseResult.success(wechatService.getLoginAccessToken(enterpriseId, param));
    }

    @PostMapping("/sendMessage")
    public ResponseResult<Boolean> sendMessage(@Validated @RequestBody WechatMessageRequest param){
        return ResponseResult.success(wechatService.sendMessage(param));
    }

    private boolean handleWechatOperation(String signature, Long timestamp, String nonce) {
        String[] array = new String[]{"36e1a5072c78359066ed7715f5ff3da8", timestamp.toString(), nonce};
        Arrays.sort(array);
        String join = String.join("", array);
        log.info("join:{}",join);
        String sha1Hex = DigestUtil.sha1Hex(join);
        return sha1Hex.equalsIgnoreCase(signature);
    }



}
