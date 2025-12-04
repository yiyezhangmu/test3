package com.coolcollege.intelligent.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.sync.vo.QywxPayOrderVo;
import com.coolcollege.intelligent.dao.order.EnterpriseOrderMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.OrderStatusEnum;
import com.coolcollege.intelligent.model.order.EnterpriseOrderDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.dingtalk.DingOrderService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.order.EnterpriseOrderService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.dingtalk.api.response.OapiAppstoreInternalSkupageGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/02
 */
@Service
@Slf4j
public class EnterpriseOrderServiceImpl implements EnterpriseOrderService {
    @Autowired
    private DingOrderService dingOrderService;
    @Value("${dingding.goods.code}")
    private String goodsCode;

    @Value("${scheduler.callback.task.url}")
    private String url;

    @Resource
    private EnterpriseOrderMapper enterpriseOrderMapper;

    @Autowired
    public EnterpriseConfigService enterpriseConfigService;

    @Override
    public String getSku(String appType) {
        CurrentUser user = UserHolder.getUser();
        OapiAppstoreInternalSkupageGetResponse response = dingOrderService.getSku(user.getDingCorpId(),url, goodsCode, appType);
        if(response.isSuccess()){
            return response.getResult();
        }
        return null;
    }

    @Override
    public Boolean qywxOrderHandle(QywxPayOrderVo reqBody) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(reqBody.getPaidCorpid(), reqBody.getAppType());
        String enterpriseId = enterpriseConfigDO == null ? null : enterpriseConfigDO.getEnterpriseId();
        // 下单
        if(OrderStatusEnum.NOTPAY.getValue().equals(reqBody.getOrderStatus())){
            EnterpriseOrderDO enterpriseOrderDO = translateToEnterpriseOrderDO(reqBody, enterpriseConfigDO);
            enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(enterpriseOrderDO));
        }else if(OrderStatusEnum.PAY.getValue().equals(reqBody.getOrderStatus())){
            // 支付
            this.updatePayOrderInfo(reqBody.getOrderid(), String.valueOf(reqBody.getOrderStatus()), enterpriseId, new Date(reqBody.getPaidTime() * 1000L), reqBody.getPrice(), reqBody.getBeginTime(), reqBody.getEndTime());
        }else if(OrderStatusEnum.REFUNDED.getValue().equals(reqBody.getOrderStatus())){
            Long refundTime = reqBody.getTimeStamp();
            this.updateOrderStatus(reqBody.getOrderid(), String.valueOf(reqBody.getOrderStatus()), enterpriseId, refundTime);
        }else {
            this.updateOrderStatus(reqBody.getOrderid(), String.valueOf(reqBody.getOrderStatus()), enterpriseId, null);
        }
        return true;
    }

    @Transactional
    @Override
    public Boolean qwChangeOrderHandle(QywxPayOrderVo oldOrder, QywxPayOrderVo newOrder) {
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(newOrder.getPaidCorpid(), newOrder.getAppType());
        EnterpriseOrderDO oldOrderCheckDO = enterpriseOrderMapper.getByBizOrderId(oldOrder.getOrderid());
        EnterpriseOrderDO newOrderCheckDO = enterpriseOrderMapper.getByBizOrderId(newOrder.getOrderid());
        if (oldOrderCheckDO == null) {
            EnterpriseOrderDO oldOrderDO = translateToEnterpriseOrderDO(oldOrder, enterpriseConfigDO);
            enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(oldOrderDO));
        } else {
            this.updateOrderInfo(oldOrder);
        }
        if (newOrderCheckDO == null) {
            EnterpriseOrderDO newOrderDO = translateToEnterpriseOrderDO(newOrder, enterpriseConfigDO);
            enterpriseOrderMapper.batchInsertEnterpriseOrder(Collections.singletonList(newOrderDO));
        } else {
            this.updateOrderInfo(newOrder);
        }
        return true;
    }

    @Override
    public EnterpriseOrderDO getByBizOrderId(String bizOrderId) {
        return enterpriseOrderMapper.getByBizOrderId(bizOrderId);
    }

    @Override
    public int updateOrderStatus(String bizOrderId, String status, String enterpriseId, Long refundTime) {
        return enterpriseOrderMapper.updateOrderStatus(bizOrderId, status, enterpriseId, refundTime);
    }

    @Override
    public int updatePayOrderInfo(String bizOrderId, String status, String enterpriseId, Date payTime, Long totalActualPayFee, Long beginTime, Long endTime) {
        return enterpriseOrderMapper.updatePayOrderInfo(bizOrderId, status, enterpriseId, payTime, totalActualPayFee, beginTime, endTime);
    }

    @Override
    public int updateOrderInfo(QywxPayOrderVo orderInfo) {
        return enterpriseOrderMapper.updateOrderInfo(orderInfo);
    }

    public EnterpriseOrderDO translateToEnterpriseOrderDO(QywxPayOrderVo reqBody, EnterpriseConfigDO enterpriseConfigDO){
        log.info("translateToEnterpriseOrderDO={}", JSON.toJSONString(reqBody));
        EnterpriseOrderDO enterpriseOrderDO = new EnterpriseOrderDO();
        enterpriseOrderDO.setStatus(String.valueOf(reqBody.getOrderStatus()));
        enterpriseOrderDO.setBuyCorpId(reqBody.getPaidCorpid());
        enterpriseOrderDO.setTotalActualPayFee(reqBody.getPrice());
        if(reqBody.getPaidTime() != null){
            enterpriseOrderDO.setPayTime(new Date(reqBody.getPaidTime() * 1000L));
        }
        enterpriseOrderDO.setQuantity(reqBody.getOrderPeriod());
        enterpriseOrderDO.setBizOrderId(reqBody.getOrderid());
        enterpriseOrderDO.setOrderSource(reqBody.getAppType());
        enterpriseOrderDO.setType(reqBody.getOrderType());
        enterpriseOrderDO.setAppId(reqBody.getSuiteid());
        enterpriseOrderDO.setOperatorId(reqBody.getOperatorId());
        enterpriseOrderDO.setEditionId(reqBody.getEditionId());
        enterpriseOrderDO.setEditionName(reqBody.getEditionName());
        enterpriseOrderDO.setUserCount(reqBody.getUserCount());
        enterpriseOrderDO.setOrderPeriod(reqBody.getOrderPeriod());
        enterpriseOrderDO.setOrderTime(reqBody.getOrderTime());
        enterpriseOrderDO.setBeginTime(reqBody.getBeginTime());
        enterpriseOrderDO.setEndTime(reqBody.getEndTime());
        enterpriseOrderDO.setOrderFrom(reqBody.getOrderFrom());
        enterpriseOrderDO.setOperatorCorpid(reqBody.getOperatorCorpid());
        enterpriseOrderDO.setServiceShareAmount(reqBody.getServiceShareAmount());
        enterpriseOrderDO.setPlatformShareAmount(reqBody.getPlatformShareAmount());
        enterpriseOrderDO.setDealerShareAmount(reqBody.getDealerShareAmount());
        if(reqBody.getDealerCorpInfo() != null){
            enterpriseOrderDO.setDealerCorpInfo(JSON.toJSONString(reqBody.getDealerCorpInfo()));
            enterpriseOrderDO.setDealerCorpId(reqBody.getDealerCorpInfo().getString("corpid"));
            enterpriseOrderDO.setDealerCorpName(reqBody.getDealerCorpInfo().getString("corp_name"));
        }
        if(enterpriseConfigDO!=null){
            enterpriseOrderDO.setEnterpriseId(enterpriseConfigDO.getEnterpriseId());
        }
        return  enterpriseOrderDO;
    }

}
