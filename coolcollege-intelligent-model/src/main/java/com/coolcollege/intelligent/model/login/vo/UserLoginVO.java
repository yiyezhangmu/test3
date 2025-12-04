package com.coolcollege.intelligent.model.login.vo;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserLoginVO
 * @Description: 用户登录信息
 * @date 2021-07-16 11:33
 */
@Data
public class UserLoginVO {

    /**
     * 登录token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 是否需要完善用户信息
     */
    private Boolean isNeedImproveUserInfo;

    /**
     * 当前登录企业
     */
    private UserLoginEnterpriseVO currentEnterprise;

    /**
     * 用户企业列表
     */
    private List<UserLoginEnterpriseVO> enterpriseList;

    /**
     * 用户信息
     */
    private UserBaseInfoVO userInfo;

    private CurrentUser currentUser;

}
