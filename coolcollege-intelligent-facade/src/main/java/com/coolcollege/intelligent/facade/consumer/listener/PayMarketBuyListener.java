package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.sync.vo.PayMarketBuyVo;
import com.coolcollege.intelligent.dao.order.EnterpriseOrderConsumerMapper;
import com.coolcollege.intelligent.dao.order.EnterpriseOrderMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.order.EnterpriseOrderConsumerDO;
import com.coolcollege.intelligent.model.order.EnterpriseOrderDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 企业购买
 *
 * @author chenyupeng
 * @since 2022/2/24
 */
@Slf4j
@Service
public class PayMarketBuyListener implements MessageListener {

    @Autowired
    public EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseOrderMapper enterpriseOrderMapper;

    @Resource
    private EnterpriseOrderConsumerMapper enterpriseOrderConsumerMapper;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {

        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "PayMarketBuyListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                payMarketBuy(text);
            }catch (Exception e){
                log.error("PayMarketBuyListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }


    public void payMarketBuy(String text) {
        log.info("deal payMarketBuy, reqBody={}", text);
        PayMarketBuyVo reqBody = JSONObject.parseObject(text, PayMarketBuyVo.class);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(reqBody.getCorpId(), reqBody.getAppType());
        EnterpriseOrderDO enterpriseOrderDO = new EnterpriseOrderDO();
        enterpriseOrderDO.setGoodsCode(reqBody.getGoodsCode());
        enterpriseOrderDO.setItemCode(reqBody.getItemCode());
        enterpriseOrderDO.setTotalActualPayFee((long) (reqBody.getPayFee() * 100));
        enterpriseOrderDO.setPayTime(reqBody.getPaidtime());
        enterpriseOrderDO.setQuantity(reqBody.getSubQuantity());
        enterpriseOrderDO.setBizOrderId(reqBody.getOrderId());
        enterpriseOrderDO.setOrderSource(reqBody.getOrderCreateSource());
        enterpriseOrderDO.setAppId(reqBody.getAppId());
        if(enterpriseConfigDO!=null){
            enterpriseOrderDO.setEnterpriseId(enterpriseConfigDO.getEnterpriseId());
        }
        // 是否开通企业事件
        Boolean isOpenEnterprise = true;
        String isvOperationCode = reqBody.getIsvOperationCode();
        String buyCorpId = reqBody.getBuyCorpid();
        String distributorCorpName = reqBody.getDistributorCorpName();
        String orderCreateSource = reqBody.getOrderCreateSource();

        if (StringUtils.isNotBlank(buyCorpId)) {
            try {
                if (StringUtils.isNotBlank(isvOperationCode) && (StringUtils.isBlank(orderCreateSource) || !orderCreateSource.equalsIgnoreCase("DRP"))) {
                    // 商品二维码（地推）方式开通
                    enterpriseOrderDO.setType(1);
                    enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(enterpriseOrderDO));
                } else if (StringUtils.isNotBlank(distributorCorpName) && StringUtils.isNotBlank(orderCreateSource) && orderCreateSource.equalsIgnoreCase("DRP")) {
                    // DRP销售码（服务商）方式开通
                    enterpriseOrderDO.setType(2);
                    enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(enterpriseOrderDO));
                } else if (StringUtils.isNotBlank(reqBody.getAppId())) {
                    isOpenEnterprise = false;
                    // AppId不为空时，则为内购商品回调
                    log.info("handle reqBody inside buy callback, orderId:{}, jsonString:{}",
                            reqBody.getOrderId(), reqBody);
                    enterpriseOrderDO.setType(3);
                    //通知钉钉内购商品订单处理完成  如果后续有消耗性的订单需要先访问订单信息再区分处理
                    enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(enterpriseOrderDO));
//                    OapiAppstoreInternalOrderFinishResponse response = dingOrderService.finishOrder(reqBody.getCorpId(), Long.valueOf(reqBody.getOrderId()));
                    EnterpriseOrderConsumerDO enterpriseOrderConsumerDO = new EnterpriseOrderConsumerDO();
                    enterpriseOrderConsumerDO.setGoodsCode(enterpriseOrderDO.getGoodsCode());
                    enterpriseOrderConsumerDO.setItemCode(enterpriseOrderDO.getItemCode());
                    enterpriseOrderConsumerDO.setTotalActualPayFee(enterpriseOrderDO.getTotalActualPayFee());
                    enterpriseOrderConsumerDO.setPayTime(enterpriseOrderDO.getPayTime());
                    enterpriseOrderConsumerDO.setBuyCorpId(enterpriseOrderDO.getBuyCorpId());
                    enterpriseOrderConsumerDO.setQuantity(enterpriseOrderDO.getQuantity());
                    enterpriseOrderConsumerDO.setStatus(enterpriseOrderDO.getStatus());
                    enterpriseOrderConsumerDO.setBizOrderId(enterpriseOrderDO.getBizOrderId());
                    enterpriseOrderConsumerDO.setOrderSource(enterpriseOrderDO.getOrderSource());
                    enterpriseOrderConsumerDO.setHandlerUserName("SYSTEM");
                    enterpriseOrderConsumerDO.setHandlerUserId("SYSTEM");
                    enterpriseOrderConsumerDO.setDealStatus(1);
//                    if(response.isSuccess()&&response.getErrcode()==0){
                    enterpriseOrderConsumerMapper.batchInsertEnterpriseOrderConsumer(Collections.singletonList(enterpriseOrderConsumerDO));
//                    }
                } else {
                    // 以上两种都不是的时候，为应用市场方式开通
                    enterpriseOrderDO.setType(4);
                    enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(enterpriseOrderDO));
                }
                log.info("save reqBody isvOperationCode buyCorpId={}, isvOperationCode={}, orderCreateSource={}, distributorCorpName={}", buyCorpId, isvOperationCode, orderCreateSource, distributorCorpName);
            } catch (Exception e) {
                log.error("handle reqBody isvOperationCode error, reqbody={},e={}", reqBody, e);
            }
        }

    }
}
