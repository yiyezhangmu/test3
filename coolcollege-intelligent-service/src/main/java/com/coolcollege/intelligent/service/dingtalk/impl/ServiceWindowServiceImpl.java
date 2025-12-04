package com.coolcollege.intelligent.service.dingtalk.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.yunda.AoKangExtendDTO;
import com.coolcollege.intelligent.model.yunda.YunDaActionCardMsgDTO;
import com.coolcollege.intelligent.service.dingtalk.ServiceWindowService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * describe:
 * {
 *     "userIds":["98092168"],
 *     "mediaId": "@lADPDe7s9Q2GhBrNBDjNB4A",
 *     "msgType": "action_card",
 *     "textContent": "hello",
 *     "isToAll": false,
 *     "msgBody": {
 *         "action_card": {
 *                         "single_url": "https://dingtalk.com",
 *                         "button_list": {
 *                                 "action_url": "btn_action_url1",
 *                                 "title": "btn_title1"
 *                         },
 *                         "btn_orientation": "0",
 *                         "single_title": "single title",
 *                         "markdown": "markdown text",
 *                         "title": "title"
 *                 },
 *                 "markdown": {
 *                         "text": "markdown text",
 *                         "title": "title"
 *                 }
 *     }
 * }
 *
 *
 * @author wxp
 * @date 2023/07/06
 */
@Service
@Slf4j
public class ServiceWindowServiceImpl implements ServiceWindowService {

    // 调用消息群发接口，给目标员工或者目标群批量推送该文章消息。POST
    public static final String SERVICE_WINDOW_MSG_URL = "https://oapi.dingtalk.com/topapi/message/mass/send?access_token=";

    public static final String APPKEY = "dingrpltpepnpr7ustws";

    public static final String APPSECRET = "E1L7YngCMQ9_BGfX2Wh9FPChFe9gAXIFUqIxjpgwwvt6Nx17JAgwaaC6STcI1gyi";

    public static final String SERVICE_NUM_UNIONID = "PjPDX7Paye0bMOYaas75UAiEiE";


    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public void sendServiceWindowMsg(CoolCollegeMsgDTO dto, String messageUrl) {
        String  access_token = getAccessToken();
        String url = SERVICE_WINDOW_MSG_URL + access_token;
        YunDaActionCardMsgDTO request = new YunDaActionCardMsgDTO();
        request.setMediaId(dto.getPicUrl());
        //ak request s
        request.setUnionid("PjPDX7Paye0bMOYaas75UAiEiE");
        request.setIs_to_all(false);
        request.setMedia_id(dto.getPicUrl());
        request.setMsg_type("action_card");
        request.setUuid(UUIDUtils.get32UUID());
        request.setText_content(dto.getMessageContent());
        request.setUserid_list(String.join(",",dto.getUserIds()));
        //ak request e
        request.setMsgType("action_card");
        request.setTextContent(dto.getMessageContent());
//        YunDaActionCardMsgDTO.MsgBody msgBody = new YunDaActionCardMsgDTO.MsgBody();
//        YunDaActionCardMsgDTO.MsgBody.ActionCard action_card = new YunDaActionCardMsgDTO.MsgBody.ActionCard();
        AoKangExtendDTO.MsgBody msg_body = new AoKangExtendDTO.MsgBody();
        AoKangExtendDTO.MsgBody.ActionCard action_card = new AoKangExtendDTO.MsgBody.ActionCard();

        action_card.setSingle_title("查看详情");
        action_card.setSingle_url(messageUrl);
        action_card.setTitle(StringUtils.isNotBlank(dto.getMessageTitle())? dto.getMessageTitle() : "标题");
        action_card.setBtn_orientation(null);
        action_card.setButton_list(Collections.emptyList());
        action_card.setMarkdown(dto.getMessageContent());
        msg_body.setAction_card(action_card);
        request.setMsg_body(msg_body);
        Lists.partition(dto.getUserIds(), 20).forEach(p -> {
            request.setUserIds(p);
            String resultStr = CoolHttpClient.sendPostJsonRequest(url, JSON.toJSONString(request));
            log.info("###aokang sendServiceWindowMsg request={}, resultStr={}", JSON.toJSONString(request), resultStr);
        });
    }


    /**
     * https://open.dingtalk.com/document/orgapp/group-posts-in-the-interactive-service-window
     * @return
     */
    public String getAccessToken() {
        String accessTokenKey = "akServiceWindowAccessTokenKey";
        String accessToken = redisUtilPool.getString(accessTokenKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(APPKEY);
        request.setAppsecret(APPSECRET);
        request.setHttpMethod("GET");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenResponse response = null;
        try {
            response = client.execute(request);
        } catch (ApiException e) {
            log.error("获取服务窗access_token 异常, response:{}", JSON.toJSONString(response), e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
        redisUtilPool.setString(accessTokenKey, response.getAccessToken(), (int)(response.getExpiresIn() - Constants.THREE_HUNDRED));
        return response.getAccessToken();
    }

}
