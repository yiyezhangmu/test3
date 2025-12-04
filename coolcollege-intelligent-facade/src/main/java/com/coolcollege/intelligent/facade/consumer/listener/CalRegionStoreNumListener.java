package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.enums.yunda.YundaEnterpriseEnum;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumRecursionMsgDTO;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 门店数量计算消费者
 *
 * @author chenyupeng
 * @since 2021/12/23
 */
@Service
@Slf4j
public class CalRegionStoreNumListener implements MessageListener {

    @Autowired
    private StoreService storeService;
    @Autowired
    private RegionService regionService;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        switch (RocketMqTagEnum.getByTag(message.getTag())){
            case REGION_STORE_NUM_UPDATE:
                RegionStoreNumMsgDTO regionStoreNumMsgDTO = JSONObject.parseObject(text, RegionStoreNumMsgDTO.class);
                storeService.batchUpdateRegionStoreNum(regionStoreNumMsgDTO.getEid(),regionStoreNumMsgDTO.getRegionIdList());
                break;
            case CAL_REGION_STORE_NUM:
                RegionStoreNumRecursionMsgDTO regionStoreNumRecursionMsgDTO = JSONObject.parseObject(text, RegionStoreNumRecursionMsgDTO.class);
                if(YundaEnterpriseEnum.yundaAffiliatedCompany(regionStoreNumRecursionMsgDTO.getEid())){
                    log.info("韵达企业不处理门店数量messageId{}", message.getMsgID());
                    break;
                }
                regionService.updateRecursionRegionStoreNum(regionStoreNumRecursionMsgDTO.getEid(),regionStoreNumRecursionMsgDTO.getRegionId());
                break;
        }
        log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
        return Action.CommitMessage;
    }
}
