package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.sync.vo.QwChangeOrderVO;
import com.coolcollege.intelligent.common.sync.vo.QywxPayOrderVo;
import com.coolcollege.intelligent.service.order.EnterpriseOrderService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企微消息监听
 *
 * @author chenyupeng
 * @since 2022/2/24
 */
@Slf4j
@Service
public class QwMsgDealListener implements MessageListener {

    @Autowired
    private EnterpriseOrderService enterpriseOrderService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "QwMsgDealListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case QW_CHANGE_ORDER_QUEUE:
                        qwChangeOrderQueue(text);
                        break;
                    case QW_OPEN_ORDER_CHANGE_QUEUE:
                        qwOpenOrderChange(text);
                        break;
                }
            }catch (Exception e){
                log.error("QwMsgDealListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }

       return Action.ReconsumeLater;
    }

    public void qwOpenOrderChange(String text) {
        log.info("deal qwOpenOrderChange,reqBody={}", text);
        QywxPayOrderVo reqBody = JSONObject.parseObject(text, QywxPayOrderVo.class);
        log.info("deal qwOpenOrderChange,QywxPayOrderVo={}", JSON.toJSONString(reqBody));
        try {
            enterpriseOrderService.qywxOrderHandle(reqBody);
        } catch (Exception e) {
            log.error("handle reqBody qwOpenOrderChange error, reqbody={}", reqBody, e);
        }
    }

    public void qwChangeOrderQueue(String text) {
        log.info("deal qwChangeOrderQueue,reqBody={}", text);
        QwChangeOrderVO reqBody = JSONObject.parseObject(text, QwChangeOrderVO.class);
        log.info("deal qwOpenOrderChange,QwChangeOrderVO={}", JSON.toJSONString(reqBody));
        try {
            DataSourceHelper.reset();
            QywxPayOrderVo oldOrderDetail = JSONObject.toJavaObject(chatService.getOrderDetail(reqBody.getOldOrderId(), reqBody.getAppType()),
                    QywxPayOrderVo.class);
            QywxPayOrderVo newOrderDetail = JSONObject.toJavaObject(chatService.getOrderDetail(reqBody.getNewOrderId(), reqBody.getAppType()),
                    QywxPayOrderVo.class);
            enterpriseOrderService.qwChangeOrderHandle(oldOrderDetail, newOrderDetail);
        } catch (Exception e) {
            log.error("handle reqBody qwOpenOrderChange error, reqbody={}", reqBody, e);
        }
    }
}
