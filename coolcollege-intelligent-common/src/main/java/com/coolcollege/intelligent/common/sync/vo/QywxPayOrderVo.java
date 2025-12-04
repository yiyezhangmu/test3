package com.coolcollege.intelligent.common.sync.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 企微订单详情Vo
 * Created by wxp on 2021/12/8
 */
@Data
public class QywxPayOrderVo {

    // 应用id
    @JSONField(name = "suiteid")
    private String suiteid;

    // 应用id。（仅旧套件有该字段）
    @JSONField(name = "appid")
    private String appid;

    // 订单号
    @JSONField(name = "orderid")
    private String orderid;
    // 订单状态。0-未支付，1-已支付，2-已关闭， 3-未支付且已过期， 4-申请退款中， 5-申请退款成功， 6-退款被拒绝
    @JSONField(name = "order_status")
    private Integer orderStatus;
    // 订单类型。0-普通订单，1-扩容订单，2-续期，3-版本变更
    @JSONField(name = "order_type")
    private Integer orderType;
    // 客户企业的corpid
    @JSONField(name = "paid_corpid")
    private String paidCorpid;
    // 下单操作人员userid。如果是服务商代下单，没有该字段。
    @JSONField(name = "operator_id")
    private String operatorId;
    // 购买版本ID
    @JSONField(name = "edition_id")
    private String editionId;
    // 购买版本名字
    @JSONField(name = "edition_name")
    private String editionName;
    // 实付款金额，单位分
    @JSONField(name = "price")
    private Long price;
    // 购买的人数
    @JSONField(name = "user_count")
    private Long userCount;
    // 购买的时间，单位天
    @JSONField(name = "order_period")
    private Integer orderPeriod;
    // 下单时间
    @JSONField(name = "order_time")
    private Long orderTime;
    // 付款时间
    @JSONField(name = "paid_time")
    private Long paidTime;
    // 购买生效期的开始时间
    @JSONField(name = "begin_time")
    private Long beginTime;
    // 购买生效期的结束时间
    @JSONField(name = "end_time")
    private Long endTime;
    // 下单来源。0-客户下单；1-服务商代下单；2-代理商代下单
    @JSONField(name = "order_from")
    private Integer orderFrom;
    // 下单方corpid
    @JSONField(name = "operator_corpid")
    private String operatorCorpid;
    // 服务商分成金额，单位分
    @JSONField(name = "service_share_amount")
    private Long serviceShareAmount;
    // 平台分成金额，单位分
    @JSONField(name = "platform_share_amount")
    private Long platformShareAmount;
    // 代理商分成金额，单位分
    @JSONField(name = "dealer_share_amount")
    private Long dealerShareAmount;
    // 渠道商信息（仅当有渠道商报备后才会有此字段）
    @JSONField(name = "dealer_corp_info")
    private JSONObject dealerCorpInfo;

    @JSONField(name = "timeStamp")
    private Long timeStamp;

    private String appType;


}
