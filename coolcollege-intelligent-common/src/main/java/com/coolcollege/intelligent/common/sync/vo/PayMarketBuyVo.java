package com.coolcollege.intelligent.common.sync.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2020/1/17.
 */
@Data
public class PayMarketBuyVo {

    @JSONField(name = "corp_id")
    private String corpId;


    @JSONField(name = "EventType")
    private String eventType;


    /**
     * 用户购买套件的SuiteKey
     */
    @JSONField(name = "SuiteKey")
    private String suiteKey;

    /**
     * 购买该套件企业的corpid
     */
    private String buyCorpid;

    /**
     * 购买的商品码
     */
    private String goodCode;

    /**
     * 购买的商品规格码
     */
    private String itemCode;

    /**
     * 购买的商品规格名称
     */
    private String itemName;

    /**
     * 购买的商品规格能服务的最多企业人数
     */
    private Integer maxOfPeople;

    /**
     * 购买的商品规格能服务的最少企业人数
     */
    private Integer minOfPeople;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 下单时间
     */
    private Date paidtime;

    /**
     * 该订单的服务到期时间
     */
    private Date serviceStopTime;

    /**
     * 订单支付费用,以分为单位
     */
    private Float payFee;

    /**
     * 订单创建来源，如果来自钉钉分销系统，则值为“DRP”
     */
    private String orderCreateSource;

    /**
     * 钉钉分销系统提单价，以分为单位
     */
    private Float nominalPayFee;

    /**
     * 折扣减免费用
     */
    private Float discountFee;

    /**
     * 订单折扣
     */
    private Float discount;

    /**
     * 钉钉分销系统提单的代理商的企业corpId
     */
    private String distributorCorpId;

    /**
     * 钉钉分销系统提单的代理商的企业名称
     */
    private String distributorCorpName;

    /**
     * 企业id
     */
    private Long enterpriseId;

    /**
     * 内购商品购买数量
     */
    private Integer subQuantity;

    /**
     * 内购商品码
     */
    private String goodsCode;

    /**
     * 内购商品名称
     */
    private String goodsName;

    /**
     * 内购商品所属的主商品码
     */
    private String mainArticleCode;

    /**
     * 内购商品所属的主商品名称
     */
    private String mainArticleName;

    /**
     * 购买类型 1：购买软件 2：课程内购
     */
    private Integer type;

    private String isvOperationCode;

    private String appId;

    private String appType;


}
