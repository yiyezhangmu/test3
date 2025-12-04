package com.coolcollege.intelligent.facade.consumer.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户全量同步接口
 *
 * @author byd
 * @since 2021/12/23
 */
@Service
@Slf4j
public class FullUserSyncListener implements MessageListener {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private SyncUserFacade syncUserFacade;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String enterpriseId = new String(message.getBody());
        if(StringUtils.isBlank(enterpriseId)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "FullUserSyncListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
                EnterpriseSettingVO enterpriseSetting = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
                syncUserFacade.syncSpecifyNodeUser(enterpriseId, null, true, enterpriseConfig, enterpriseSetting);
            }catch (Exception e){
                log.error("FullUserSyncListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("FullUserSyncListener消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),enterpriseId);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
