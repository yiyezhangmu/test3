package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.unifytask.dto.CombineUpcomingCancelData;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 合并待办取消
 *
 * @author wxp
 * @since 2023/11/27
 */
@Slf4j
@Service
public class CombineUpcomingCancelListener implements MessageListener {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "CombineUpcomingCancelListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                cancelCombineUpcoming(text);
            }catch (Exception e){
                log.error("StoreSubTaskDataQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    // 取消合并待办
    public void cancelCombineUpcoming(String text) {
        log.info("cancelCombineUpcoming, reqBody={}", text);
        CombineUpcomingCancelData combineUpcomingCancelData = JSONObject.parseObject(text, CombineUpcomingCancelData.class);
        String enterpriseId = combineUpcomingCancelData.getEnterpriseId();
        String dingCorpId = combineUpcomingCancelData.getDingCorpId();
        String appType = combineUpcomingCancelData.getAppType();
        Long unifyTaskId = combineUpcomingCancelData.getUnifyTaskId();
        Long loopCount = combineUpcomingCancelData.getLoopCount();
        String handleUserId = combineUpcomingCancelData.getHandleUserId();
        TaskStoreLoopQuery query = new  TaskStoreLoopQuery();
        query.setHandleUserId(handleUserId);
        query.setUnifyTaskId(unifyTaskId);
        query.setLoopCount(loopCount);
        UnifySubStatisticsDTO statistics = elasticSearchService.getHandleTaskStoreCount(enterpriseId, query);
        String taskKey = Constants.TASKNOTICECOMBINE + "_" + enterpriseId + "_"
                + unifyTaskId + "_" + loopCount + "_" + UnifyNodeEnum.FIRST_NODE.getCode()
                + "_" + MD5Util.md5(JSONUtil.toJsonStr(Arrays.asList(handleUserId)));
        log.info("cancelCombineUpcoming开始删除用户合并待办：statistics:{}, taskKey:{}", JSONObject.toJSONString(statistics), taskKey);
        if(statistics.getAll() > 1 && statistics.getHandle() == 0 ){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("enterpriseId", enterpriseId);
            jsonObject.put("corpId", dingCorpId);
            jsonObject.put("taskKey", taskKey);
            jsonObject.put("appType", appType);
            jsonObject.put("userIds", Arrays.asList(handleUserId));
            simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
        }
    }

}
