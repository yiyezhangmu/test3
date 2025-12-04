package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.facade.NewSyncFacade;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.model.region.dto.AsyncDingRequestDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钉钉同步
 *
 * @author chenyupeng
 * @since 2022/2/24
 */
@Slf4j
@Service
public class DingSyncAllDataQueueListener implements MessageListener {

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private SyncUserFacade syncUserFacade;

    @Autowired
    private NewSyncFacade newSyncFacade;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "DingSyncAllDataQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.SYNC_LOCK_TIMES);
        if(lock){
            try {
                dingSyncAllDataQueue(text);
            }catch (Exception e){
                log.error("DingSyncAllDataQueueListener consume dealAddressBookChange error",e);
                return Action.ReconsumeLater;
            } finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void dingSyncAllDataQueue(String text) {
        log.info("dingSyncAllDataQueue, reqBody={}", text);
        AsyncDingRequestDTO reqBody = JSONObject.parseObject(text, AsyncDingRequestDTO.class);
        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(reqBody.getEid());
        //节点同步不限制次数
        if (reqBody.getRegionId()==null&&StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
            log.info("一天内只能同步一次 eid:{}",reqBody.getEid());
            return;
        }
        //加入锁
        //todo 时间设置为1小时
        redisUtilPool.setString(eidLockKey, reqBody.getEid(),  60*60);
        //
        if(!AppTypeEnum.ONE_PARTY_APP.getValue().equals(reqBody.getAppType())) {
            newSyncFacade.syncDeptAndUser(reqBody.getEid(),reqBody.getUserName(),reqBody.getUserId(), reqBody.getRegionId());
        }else {
            // 门店通应用同步
            syncUserFacade.syncAllForOneParty(reqBody.getEid(), reqBody.getUserName(), reqBody.getUserId(),
                    reqBody.getDingCorpId(), reqBody.getDbName());
        }

    }

}
