package com.coolcollege.intelligent.service.login;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiGetUserAccessTokenDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserAccessTokenVO;
import com.coolcollege.intelligent.model.login.YNUserLoginDTO;
import com.coolcollege.intelligent.model.login.request.AskBotLoginRequest;
import com.coolcollege.intelligent.model.login.request.MclzLoginRequest;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    Object isvLogin(String userId, String corpId, Boolean needRefreshToken , String appType, String avatar);

    /**
     * 重新获取token
     * @param userId
     * @param eid
     * @param request
     * @param loginType 登陆类型
     * @param needRefreshToken 是否需要重新获取token
     * @param appType
     * @author: xugangkun
     * @return java.lang.Object
     * @date: 2021/11/8 15:12
     */
    Object refreshLogin(String userId, String eid, HttpServletRequest request,String loginType,Boolean needRefreshToken, String appType, String loginWay);

    Object wxIsvLogin(String code, String corpId, String appType, HttpServletRequest request, String loginWay);

    Object wxQrcodeLogin(String authCode, String corpId, String appType, HttpServletRequest request, String loginWay);

    Object yuNiLogin(YNUserLoginDTO param);

    /**
     * 获取用户accessToken
     * @param param
     * @return
     */
    UserAccessTokenVO getUserAccessToken(String enterpriseId, OpenApiGetUserAccessTokenDTO param);

    /**
     * 明厨亮灶小程序校验用户是否注册过
     */
    Boolean mclzRegisteredVerify(MclzLoginRequest request);

    /**
     * 明厨亮灶小程序登录
     * @param request 登录请求对象
     * @return java.lang.Object
     */
    Object mclzLogin(MclzLoginRequest request);

    String askBotLogin(AskBotLoginRequest request);


}
