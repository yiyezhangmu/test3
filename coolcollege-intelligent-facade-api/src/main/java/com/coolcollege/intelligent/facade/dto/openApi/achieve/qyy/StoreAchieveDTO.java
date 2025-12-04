package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: StoreAchieveDTO
 * @Description:业绩宽表数据
 * @date 2023-03-30 15:20
 */
@Data
public class StoreAchieveDTO {

    /**
     * 日期(yyyy-MM-dd)
     */
    private String salesDt;

    /**
     * 总销售额
     */
    private BigDecimal salesAmt;

    /**
     * 销售额增长率
     */
    private BigDecimal salesAmtZzl;

    /**
     * 客单价
     */
    private BigDecimal cusPrice;

    /**
     * 客单价增长率
     */
    private BigDecimal cusPriceZzl;

    /**
     * 毛利率
     */
    private BigDecimal profitRate;

    /**
     * 毛利率增长率
     */
    private BigDecimal profitZzl;

    /**
     * 连带率
     */
    private BigDecimal jointRate;

    /**
     * 连带率增长率
     */
    private BigDecimal jointRateZzl;

    /**
     * 分公司排名
     */
    private Integer topComp;

    /**
     * 全国排名
     */
    private Integer topTot;

    /**
     * 完成率
     */
    private BigDecimal salesRate;

    /**
     * 同期增长率
     */
    private BigDecimal yoySalesZzl;

    /**
     * 开单数量
     */
    private Integer billNum;

    /**
     * 上报时间(yyyy-MM-dd HH:mm:ss)
     */
    private Date etlTm;

    /**
     * 爆款商品id
     */
    private String goodsId;

}
