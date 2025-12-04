package com.coolcollege.intelligent.service.login.impl.strategy;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.service.login.impl.LoginBaseService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: SmsLoginService
 * @Description: 验证码登录实现类
 * @date 2021-07-16 11:45
 */
@Service("smsLoginService")
@Slf4j
public class SmsLoginService extends LoginBaseService {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public ResponseResult userLogin(UserLoginDTO param, List<UserLoginDO> allUsers) {
        String mobile = param.getMobile();
        String smsCode = param.getSmsCode();
        if(StringUtils.isBlank(smsCode)){
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_MISSING);
        }
        String smsCodeKey = SmsCodeTypeEnum.LOGIN + ":"+ mobile;
        String code = redisUtilPool.getString(smsCodeKey);
        if(StringUtils.isBlank(code)){
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if(!smsCode.equals(code)){
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success(getUserLoginVO(allUsers, param.getEnterpriseId()));
    }

}
