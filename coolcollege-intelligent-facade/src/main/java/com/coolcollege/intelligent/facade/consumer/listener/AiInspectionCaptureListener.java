package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodDealDTO;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodStoreDealDTO;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import com.coolcollege.intelligent.service.inspection.impl.AiInspectionCapturePictureServiceImpl;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2025-10-15 10:55
 */
@Service
@Slf4j
public class AiInspectionCaptureListener implements MessageListener {

    @Resource
    private AiInspectionCapturePictureService aiInspectionCapturePictureService;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if (message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)) {
            //超过最大消费次数
            return Action.CommitMessage;
        }
        String text = new String(message.getBody());
        if (StringUtils.isBlank(text)) {
            return Action.CommitMessage;
        }
        log.info("AiInspectionCaptureListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), text);
        String lockKey = "AiInspectionCaptureListener:" + message.getMsgID();

        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.ENTERPRISE_OPEN_LOCK_TIMES);
        if (lock) {
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())) {
                    case AI_INSPECTION_DATA_DEAL:
                        AiInspectionTimePeriodDealDTO aiInspectionTimePeriodDealDTO = JSONObject.parseObject(text, AiInspectionTimePeriodDealDTO.class);
                        DataSourceHelper.reset();
                        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(aiInspectionTimePeriodDealDTO.getEnterpriseId());
                        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                        aiInspectionCapturePictureService.decomposeStores(aiInspectionTimePeriodDealDTO.getEnterpriseId(), aiInspectionTimePeriodDealDTO);
                        break;
                    case AI_INSPECTION_STORE_TASK:
                        AiInspectionTimePeriodStoreDealDTO timePeriodStoreDealDTO = JSONObject.parseObject(text, AiInspectionTimePeriodStoreDealDTO.class);
                        DataSourceHelper.reset();
                        config = enterpriseConfigMapper.selectByEnterpriseId(timePeriodStoreDealDTO.getEnterpriseId());
                        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                        try {
                            aiInspectionCapturePictureService.storeCapture(timePeriodStoreDealDTO.getEnterpriseId(), timePeriodStoreDealDTO);
                        } catch (Exception e) {
                            log.error("AiInspectionCaptureListener#AI_INSPECTION_STORE_TASK#error", e);
                        }
                        break;
                    default:
                        break;
                }
                return Action.CommitMessage;
            } catch (Exception e) {
                log.error("AiInspectionCaptureListener#has exception", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.ReconsumeLater;
    }
}
