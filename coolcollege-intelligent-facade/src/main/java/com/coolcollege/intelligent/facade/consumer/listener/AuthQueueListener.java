package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.sync.vo.AuthMsg;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.sync.SyncUtils;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 授权队列
 *
 * @author chenyupeng
 * @since 2022/2/24
 */
@Slf4j
@Service
public class AuthQueueListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtil;

    @Autowired
    private SyncFacade syncFacade;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    /**
     * 企业授权最大有效期(单位是秒)
     */
    @Value("${auth.expired}")
    private Integer authExpired;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "AuthQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.SYNC_LOCK_TIMES);

        if(lock){
            try {
                onAuthMsg(text);
            }catch (Exception e){
                log.error("DingMsgDealListener consume dealAddressBookChange error",e);
                return Action.ReconsumeLater;
            } finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void onAuthMsg(String msg) {
        if (StringUtils.isBlank(msg)) {
            return;
        }
        log.info("onAuthMsg {}", msg);
        AuthMsg authMsg = null;
        try {
            authMsg = JSON.parseObject(msg, AuthMsg.class);
        } catch (Exception e) {
            log.error("invalid auth msg={}", msg);
        }
        if (null == authMsg) {
            return;
        }

        String authKey = SyncUtils.getAuthKey(authMsg.getCorpId());
        String value = redisUtil.getString(authKey);
        if (StringUtils.isBlank(value)) {
            redisUtil.setString(authKey, authMsg.getCorpId(), authExpired);
            if (Objects.isNull(authMsg.getScopeChange()) || !authMsg.getScopeChange()) {
                syncFacade.start(authMsg.getCorpId(), authMsg.getAppType(), true);
            } else {
                //新的通讯录授权变更后的处理
                syncFacade.scopeChange(authMsg.getCorpId(), authMsg.getAppType(), true, authMsg.getPermanentCode());
            }

        } else {
            simpleMessageService.send(msg, RocketMqTagEnum.AUTH_QUEUE);
        }
    }
}
