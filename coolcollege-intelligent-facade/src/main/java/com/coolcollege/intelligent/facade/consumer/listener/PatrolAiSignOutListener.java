package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolAiSignOutDTO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSignOutParam;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author chenyupeng
 * @since 2022/4/22
 */
@Service
@Slf4j
public class PatrolAiSignOutListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private PatrolStoreService patrolStoreService;

    @Autowired
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private EnterpriseConfigMapper configMapper;

    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        PatrolAiSignOutDTO patrolAiSignOutDTO = JSONUtil.toBean(text, PatrolAiSignOutDTO.class);
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "PatrolAiSignOutListener:" + Constants.UNDERLINE + patrolAiSignOutDTO.getEid() + Constants.UNDERLINE + patrolAiSignOutDTO.getUnifyTaskId();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                String eid = patrolAiSignOutDTO.getEid();
                //签退
                PatrolStoreSignOutParam param = new PatrolStoreSignOutParam();
                param.setBusinessId(patrolAiSignOutDTO.getBusinessId());
                param.setSignOutStatus(1);
                DataSourceHelper.reset();
                // 企业配置
                EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                        enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(eid);
                EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(eid);

                EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);
                DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                patrolStoreService.signOut(config.getDingCorpId(),eid,param,storeCheckSettingDO, patrolAiSignOutDTO.getUserId(),patrolAiSignOutDTO.getUserName(),patrolAiSignOutDTO.getAppType(), enterpriseSettingDO);

            }catch (Exception e){
                log.error("PatrolAiSignOutListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }else {
            log.info("AI巡店签退获取锁失败 tryTimes:{}, messageId:{},unifyTaskId:{}",message.getReconsumeTimes(),message.getMsgID(),patrolAiSignOutDTO.getUnifyTaskId());
        }
        return Action.ReconsumeLater;
    }
}
