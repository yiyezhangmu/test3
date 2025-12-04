package com.coolcollege.intelligent.controller.video;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.model.video.platform.yingshi.webhook.WebhookMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 萤石开放平台链接  https://open.ys7.com/help/566
 */
@Slf4j
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/yingshiyunMsgPush")
public class YingshiyunMsgPushController {

    @RequestMapping(value = "/webhook")
    public ResponseEntity<String> VipWebhook(@RequestHeader HttpHeaders header, @RequestBody String body) {
        WebhookMessage receiveMessage = null;
        log.info("消息获取时间:{}, 请求头:{},请求体:{}",System.currentTimeMillis(), JSON.toJSONString(header),body);
        System.out.println("收到的消息:"+body);
        try {
            receiveMessage = JSON.parseObject(body, WebhookMessage.class);
        } catch (Exception e) {
            //异常处理
            e.printStackTrace();
        }
        Map<String, String> result = new HashMap<>(1);
        assert receiveMessage != null;
        String messageId = receiveMessage.getHeader().getMessageId();
        result.put("messageId", messageId);
        final ResponseEntity<String> resp = ResponseEntity.ok(JSON.toJSONString(result));
        log.info("返回的信息:{}",JSON.toJSONString(resp));
        return resp;
    }

}
