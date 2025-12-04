package com.coolcollege.intelligent.dao.order;

import com.coolcollege.intelligent.common.sync.vo.QywxPayOrderVo;
import com.coolcollege.intelligent.model.order.EnterpriseOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zyp
 */
@Mapper
public interface EnterpriseOrderMapper {

    /**
     *
     * @param enterpriseOrderDOList
     */
    void batchInsertEnterpriseOrder(@Param("list") List<EnterpriseOrderDO> enterpriseOrderDOList);

    EnterpriseOrderDO getByBizOrderId(@Param("bizOrderId")String bizOrderId);
    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("bizOrderId") String bizOrderId,
                          @Param("status") String status,
                          @Param("enterpriseId") String enterpriseId,
                          @Param("refundTime") Long refundTime);

    /**
     * 更新支付状态
     */
    int updatePayOrderInfo(@Param("bizOrderId") String bizOrderId,
                          @Param("status") String status,
                          @Param("enterpriseId") String enterpriseId,
                          @Param("payTime") Date payTime,
                          @Param("totalActualPayFee") Long totalActualPayFee,
                          @Param("beginTime") Long beginTime,
                          @Param("endTime") Long endTime);

    /**
     * 更新订单数据
     * @param orderInfo
     * @author: xugangkun
     * @return int
     * @date: 2022/1/26 17:18
     */
    int updateOrderInfo(@Param("orderInfo") QywxPayOrderVo orderInfo);
}
