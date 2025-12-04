package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseInitDTO;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * 企业开通初始化
 *
 * @author chenyupeng
 * @since 2022/1/26
 */
@Slf4j
@Service
public class EnterpriseInitListener implements MessageListener {
    @Resource
    private EnterpriseInitService enterpriseInitService;
    @Resource
    private WeComService weComService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if(message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
            return Action.CommitMessage;
        }
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "EnterpriseInitDataSync:" + message.getMsgID();
        EnterpriseInitDTO enterpriseInitDTO = JSONObject.parseObject(text, EnterpriseInitDTO.class);
        log.info("EnterpriseInitListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), JSONObject.toJSONString(enterpriseInitDTO));
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.ENTERPRISE_OPEN_LOCK_TIMES);
        if (lock) {
            try {
                String appType = enterpriseInitDTO.getAppType();
                String enterpriseStatusKey = MessageFormat.format(RedisConstant.ENTERPRISE_OPEN_STATUS_KEY, enterpriseInitDTO.getCorpId(), enterpriseInitDTO.getAppType());
                //更新企业开通缓存状态
                redisUtilPool.setString(enterpriseStatusKey, String.valueOf(Constants.STATUS.NORMAL), RedisConstant.ONE_DAY_SECONDS);
                enterpriseInitService.enterpriseInit(enterpriseInitDTO.getCorpId(), AppTypeEnum.getAppType(enterpriseInitDTO.getAppType()),
                        enterpriseInitDTO.getEid(), enterpriseInitDTO.getDbName(), enterpriseInitDTO.getUserId());
                if (AppTypeEnum.isQwType(enterpriseInitDTO.getAppType()) || AppTypeEnum.isDingType(enterpriseInitDTO.getAppType()) ||
                        AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseInitDTO.getAppType())
                        || AppTypeEnum.ONE_PARTY_APP2.getValue().equals(enterpriseInitDTO.getAppType())) {
                    enterpriseInitService.sendBossMessage(enterpriseInitDTO.getCorpId(), AppTypeEnum.getAppType(enterpriseInitDTO.getAppType()));
                    log.info("EnterpriseInitListener consume end data :{}", JSONObject.toJSONString(enterpriseInitDTO));
                }
                if (StringUtils.equals(appType, AppTypeEnum.WX_APP2.getValue())) {
                    weComService.sendOpenSucceededMsg(enterpriseInitDTO.getEid());
                }
                if(AppTypeEnum.FEI_SHU.getValue().equals(appType)){
                    enterpriseInitService.sendOpenSucceededMsg(enterpriseInitDTO.getCorpId(), enterpriseInitDTO.getAppType(), Arrays.asList(enterpriseInitDTO.getUserId()));
                }
                log.info("企业开通成功：corpId:{}，appType:{}", enterpriseInitDTO.getCorpId(), appType);
                return Action.CommitMessage;
            } catch (Exception e) {
                log.error("has exception", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.CommitMessage;
    }


}









