package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolstore.base.enums.RocketMqTagEnum;
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
public class EnterpriseScriptListener implements MessageListener {

    @Resource
    private EnterpriseInitService enterpriseInitService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        log.info("EnterpriseScriptListener messageId:{}, msg:{}", message.getMsgID(), text);
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        switch (RocketMqTagEnum.getByTag(message.getTag())){
            case ENTERPRISE_OPEN_ENTERPRISE_RUN_SCRIPT:
                log.info("run Enterprise Script start");
                EnterpriseOpenMsg msg = null;
                try {
                    msg = JSON.parseObject(text, EnterpriseOpenMsg.class);
                } catch (Exception e) {
                    log.error("invalid auth msg={}", text);
                }
                enterpriseInitService.runEnterpriseScript(msg);
                break;
            default:
                break;
        }
        return Action.CommitMessage;
    }
}
