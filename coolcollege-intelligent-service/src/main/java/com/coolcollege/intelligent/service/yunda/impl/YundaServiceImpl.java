package com.coolcollege.intelligent.service.yunda.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.yunda.YunDaActionCardMsgDTO;
import com.coolcollege.intelligent.service.yunda.YundaService;
import com.google.common.collect.Lists;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
public class YundaServiceImpl implements YundaService {

    @Override
    public void sendServiceWindowMsg(CoolCollegeMsgDTO dto, String messageUrl) {
        String url = "http://nyddx.yundasys.com/api/v2/dingService";
        YunDaActionCardMsgDTO request = new YunDaActionCardMsgDTO();
        request.setMediaId(dto.getPicUrl());
        request.setMsgType("action_card");
        request.setTextContent(dto.getMessageContent());
        YunDaActionCardMsgDTO.MsgBody msgBody = new YunDaActionCardMsgDTO.MsgBody();
        YunDaActionCardMsgDTO.MsgBody.ActionCard action_card = new YunDaActionCardMsgDTO.MsgBody.ActionCard();
        action_card.setSingle_title("查看详情");
        action_card.setSingle_url(messageUrl);
        action_card.setTitle(StringUtils.isNotBlank(dto.getMessageTitle())? dto.getMessageTitle() : "标题");
        action_card.setBtn_orientation(null);
        action_card.setButton_list(Collections.emptyList());
        action_card.setMarkdown(dto.getMessageContent());
        msgBody.setAction_card(action_card);
        request.setMsgBody(msgBody);
        Lists.partition(dto.getUserIds(), 20).forEach(p -> {
            request.setUserIds(p);
            String resultStr = CoolHttpClient.sendPostJsonRequest(url, JSON.toJSONString(request));
            log.info("###yunda sendServiceWindowMsg request={}, resultStr={}", JSON.toJSONString(request), resultStr);
        });
    }

}
