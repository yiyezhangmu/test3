package com.coolcollege.intelligent.model.order;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/02
 */
@Data
public class EnterpriseOrderDO {
    private Long id;
    private String enterpriseId;

    private String goodsCode;
    private String itemCode;
    private Long totalActualPayFee;
    private Date payTime;
    private String buyCorpId;
    private Integer quantity;
    private String status;
    private String bizOrderId;
    private String orderSource;

    /**
     * 应用ID
     */
    private String appId;
    /**
     * 订单类型
     */
    private Integer type;

    /**
     * 下单操作人员userid。如果是服务商代下单，没有该字段。
     */
    private String operatorId;
    /**
     * 购买版本ID
     */
    private String editionId;
    /**
     * 购买版本名字
     */
    private String editionName;
    /**
     * 购买的人数
     */
    private Long userCount;
    /**
     * 购买的时间，单位天
     */
    private Integer orderPeriod;

    /**
     * 下单时间
     */
    private Long orderTime;

    /**
     * 购买生效期的开始时间
     */
    private Long beginTime;
    /**
     * 购买生效期的结束时间
     */
    private Long endTime;
    /**
     * 下单来源。0-客户下单；1-服务商代下单；2-代理商代下单
     */
    private Integer orderFrom;
    /**
     * 下单方corpid
     */
    private String operatorCorpid;
    /**
     * 服务商分成金额，单位分
     */
    private Long serviceShareAmount;
    /**
     * 平台分成金额，单位分
     */
    private Long platformShareAmount;

    /**
     * 代理商分成金额，单位分
     */
    private Long dealerShareAmount;
    /**
     * 渠道商信息（仅当有渠道商报备后才会有此字段）
     */
    private String dealerCorpInfo;
    /**
     * 代理商corpid
     */
    private String dealerCorpId;
    /**
     * 代理商名
     */
    private String dealerCorpName;
    /**
     * 退款时间
     */
    private Long refundTime;



}
