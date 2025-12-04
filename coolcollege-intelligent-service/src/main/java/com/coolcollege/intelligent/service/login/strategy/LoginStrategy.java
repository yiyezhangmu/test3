package com.coolcollege.intelligent.service.login.strategy;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.login.SwitchEnterpriseDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;

/**
 * @author zhangchenbiao
 * @FileName: LoginStrategy
 * @Description: 登录策略基本类
 * @date 2021-07-16 11:01
 */
public interface LoginStrategy {

    /**
     * 登录基础方法
     * @param param
     * @return
     */
    ResponseResult login(UserLoginDTO param);

    /**
     * 切换企业登录
     * @param param
     * @return
     */
    ResponseResult switchLoginEnterprise(SwitchEnterpriseDO param, String mobile);

    /**
     * 获取当前登录用户的企业列表
     * @return
     */
    ResponseResult getCurrentLoginUserEnterpriseList(String mobile);


}
