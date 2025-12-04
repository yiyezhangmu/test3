package com.coolcollege.intelligent.service.wechat;

import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.wechat.request.WechatLoginRequest;
import com.coolcollege.intelligent.model.wechat.request.WechatMessageRequest;
import com.coolcollege.intelligent.model.wechat.vo.WechatLoginUserInfoVO;
import com.coolcollege.intelligent.model.wechat.vo.WechatSignatureVO;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: WechatService
 * @Description:
 * @date 2023-10-17 14:58
 */
public interface WechatService {

    /**
     * 微信获取签名
     * @param appId
     * @param url
     * @return
     */
    WechatSignatureVO getSignature(String appId, String url);

    /**
     * 获取accessToken
     * @param appId
     * @return
     */
    String getWechatAccessToken(String appId);

    /**
     * 获取用户信息
     * @param enterpriseId
     * @param appId
     * @param code
     * @return
     */
    WechatLoginUserInfoVO getUserInfo(String enterpriseId, String appId, String code);

    /**
     * 获取用户登录信息
     * @param enterpriseId
     * @param param
     * @return
     */
    WechatLoginUserInfoVO getLoginAccessToken(String enterpriseId, WechatLoginRequest param);

    /**
     * 发送公众号消息
     * @param param
     * @return
     */
    Boolean sendMessage(WechatMessageRequest param);

    /**
     * 发送公众号消息
     * @param enterpriseConfig
     * @param dto
     * @param urlParam
     */
    void sendWXMsg(EnterpriseConfigDO enterpriseConfig, CoolCollegeMsgDTO dto, String urlParam);


    void sendWXMsg(EnterpriseConfigDO enterpriseConfig, List<String> userIds, String title, String content, String urlParam, String outBusinessId, String storeId);

    void sendWXMsg(String enterpriseId, List<String> userIds, String title, String content, String urlParam);

    void sendWXMsg(EnterpriseConfigDTO enterpriseConfig, List<String> userIds, String title, String content, String urlParam);
}
