package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.sync.vo.StoreGroupReqBody;
import com.coolcollege.intelligent.service.store.StoreGroupService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 执行企业端脚本消息监听
 * @author ：xugangkun
 * @date ：2022/2/11 15:32
 */
@Service
@Slf4j
public class StoreGroupEventListener implements MessageListener {

    @Resource
    private StoreGroupService storeGroupService;

    @SneakyThrows
    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        log.info("StoreGroupEventListener messageId:{}, msg:{}", message.getMsgID(), text);
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        StoreGroupReqBody reqBody = JSONObject.parseObject(text, StoreGroupReqBody.class);
        storeGroupService.handleGroupEvent(reqBody);

        return Action.CommitMessage;
    }
}
