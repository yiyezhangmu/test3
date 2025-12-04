package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.model.activity.dto.ActivityMqMessageDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.activity.ActivityService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 门店数量计算消费者
 *
 * @author chenyupeng
 * @since 2021/12/23
 */
@Service
@Slf4j
public class ActivityListener implements MessageListener {

    @Autowired
    private ActivityService activityService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());
        ActivityMqMessageDTO activityMqMessage = JSONObject.parseObject(text, ActivityMqMessageDTO.class);
        if(Objects.isNull(activityMqMessage) || StringUtils.isBlank(activityMqMessage.getEnterpriseId())){
            return Action.CommitMessage;
        }
        String enterpriseId = activityMqMessage.getEnterpriseId();
        try {
            EnterpriseConfigDTO enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return Action.CommitMessage;
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            switch (RocketMqTagEnum.getByTag(message.getTag())){
                case ACTIVITY_COMMENT_COUNT:
                    activityService.updateCommentCount(activityMqMessage);
                    break;
                case ACTIVITY_REPLY_COUNT:
                    activityService.updateReplyCount(activityMqMessage);
                    break;
                case ACTIVITY_LIKE_COUNT:
                    activityService.updateLikeCount(activityMqMessage);
                    break;
                case ACTIVITY_STATUS_UPDATE:
                    activityService.updateActivityStatus(activityMqMessage.getEnterpriseId(), activityMqMessage.getActivityId());
                    break;
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
        return Action.CommitMessage;
    }
}
