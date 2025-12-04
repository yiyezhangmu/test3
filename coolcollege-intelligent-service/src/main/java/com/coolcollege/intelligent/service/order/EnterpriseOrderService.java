package com.coolcollege.intelligent.service.order;

import com.coolcollege.intelligent.common.sync.vo.QywxPayOrderVo;
import com.coolcollege.intelligent.model.order.EnterpriseOrderDO;

import java.util.Date;

public interface EnterpriseOrderService {
    String getSku(String appType);

    Boolean qywxOrderHandle(QywxPayOrderVo reqBody);

    /**
     * 改单处理
     * @param oldOrder 旧订单
     * @param newOrder 新订单
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2022/1/26 10:57
     */
    Boolean qwChangeOrderHandle(QywxPayOrderVo oldOrder, QywxPayOrderVo newOrder);

    EnterpriseOrderDO getByBizOrderId(String bizOrderId);
    /**
     * 更新订单状态
     */
    int updateOrderStatus(String bizOrderId, String status, String enterpriseId, Long refundTime);

    int updatePayOrderInfo(String bizOrderId, String status, String enterpriseId, Date payTime, Long totalActualPayFee,
                           Long beginTime, Long endTime);

    /**
     * 更新订单信息
     * @param orderInfo
     * @author: xugangkun
     * @return int
     * @date: 2022/1/26 17:30
     */
    int updateOrderInfo(QywxPayOrderVo orderInfo);

}
