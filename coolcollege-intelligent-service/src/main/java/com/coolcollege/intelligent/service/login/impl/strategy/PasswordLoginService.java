package com.coolcollege.intelligent.service.login.impl.strategy;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.service.login.impl.LoginBaseService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PasswordLoginService
 * @Description: 账号密码登录实现类
 * @date 2021-07-16 11:45
 */
@Service("passwordLoginService")
@Slf4j
public class PasswordLoginService extends LoginBaseService {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public ResponseResult userLogin(UserLoginDTO param, List<UserLoginDO> allUsers) {
        if(StringUtils.isBlank(param.getPassword())){
            return ResponseResult.fail(ErrorCodeEnum.PASSWORD_MISSING);
        }
        //获取存在密码的用户
        List<UserLoginDO> havePasswordUsers = allUsers.stream().filter(o -> StringUtils.isNotBlank(o.getPassword())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(havePasswordUsers)){
            return ResponseResult.fail(ErrorCodeEnum.IMPROVE_USER_INFO);
        }
        String password = MD5Util.md5(param.getPassword() + Constants.USER_AUTH_KEY);
        //过滤相同密码的用户
        List<UserLoginDO> samePasswordUsers = havePasswordUsers.stream().filter(o -> password.equals(o.getPassword())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(samePasswordUsers)){
            //获取密码尝试登录错误次数
            String errorPasswordCountKey = MessageFormat.format(RedisConstant.ERROR_PASSWORD_COUNT_KEY, LocalDate.now(), param.getMobile());
            Long errorNum = redisUtilPool.incrby(errorPasswordCountKey, 1);
            redisUtilPool.expire(errorPasswordCountKey, 24 * 60 * 60);
            if(errorNum == 1){
                return ResponseResult.fail(ErrorCodeEnum.PASSWORD_ERROR);
            }
            return ResponseResult.fail(ErrorCodeEnum.PASSWORD_ERROR_MULTI, errorNum);
        }
        //获取密码相同的用户所在的企业
        return ResponseResult.success(getUserLoginVO(samePasswordUsers, param.getEnterpriseId()));
    }

}
