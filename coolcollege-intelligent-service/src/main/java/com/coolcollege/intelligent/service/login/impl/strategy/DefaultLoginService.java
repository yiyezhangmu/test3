package com.coolcollege.intelligent.service.login.impl.strategy;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.service.login.impl.LoginBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: DefaultLoginService
 * @Description: 默认登录处理
 * @date 2021-07-19 17:28
 */
@Service("defaultLoginService")
@Slf4j
public class DefaultLoginService extends LoginBaseService {

    @Override
    public ResponseResult userLogin(UserLoginDTO param, List<UserLoginDO> allUsers) {
        return ResponseResult.fail(ErrorCodeEnum.LOGIN_TYPE_NOT_SUPPORT);
    }

}
