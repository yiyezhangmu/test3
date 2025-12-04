package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseInitDeptOrderDTO;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 钉钉的部门次序值的补全
 *
 * @author chenyupeng
 * @since 2022/1/26
 */
@Slf4j
@Service
public class EnterpriseInitDeptOrderListener implements MessageListener {
    @Resource
    private EnterpriseInitService enterpriseInitService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if(message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
        }
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        EnterpriseInitDeptOrderDTO initDeptOrderDTO = JSONObject.parseObject(text, EnterpriseInitDeptOrderDTO.class);
        log.info("EnterpriseInitDeptOrderListener messageId:{}, receive data :{}", message.getMsgID(), JSONObject.toJSONString(initDeptOrderDTO));
        boolean lock = redisUtilPool.setNxExpire(message.getMsgID(), message.getMsgID(), CommonConstant.ENTERPRISE_OPEN_LOCK_TIMES);
        if (lock) {
            try {
                enterpriseInitService.enterpriseInitDeptOrder(initDeptOrderDTO.getCorpId(), AppTypeEnum.getAppType(initDeptOrderDTO.getAppType()),
                        initDeptOrderDTO.getEid(), initDeptOrderDTO.getDbName(), initDeptOrderDTO.getDeptIds());
                log.info("EnterpriseInitDeptOrderListener consume end data :{}", JSONObject.toJSONString(initDeptOrderDTO));
                return Action.CommitMessage;
            } catch (Exception e) {
                log.error("has exception", e);
            } finally {
                redisUtilPool.delKey(message.getMsgID());
            }
        }
        return Action.ReconsumeLater;
    }


}









