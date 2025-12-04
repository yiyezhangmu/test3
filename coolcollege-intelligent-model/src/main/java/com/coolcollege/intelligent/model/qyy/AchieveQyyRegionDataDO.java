package com.coolcollege.intelligent.model.qyy;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-03-29 04:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchieveQyyRegionDataDO implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("第三方唯一id")
    private String thirdDeptId;

    @ApiModelProperty("组织节点名称")
    private String deptName;

    @ApiModelProperty("节点类型 总部、分子公司、门店")
    private String nodeType;

    @ApiModelProperty("时间类型：year:年 month:月 week:周 day:天")
    private String timeType;

    @ApiModelProperty("业务日期 年yyyy, 月yyyymm 周取周一对应yyyymmdd, 日yyyymmdd")
    private String timeValue;

    @ApiModelProperty("销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("销售额增长率(这里才是同期增长率)")
    private BigDecimal salesAmtZzl;

    @ApiModelProperty("客单价")
    private BigDecimal cusPrice;

    @ApiModelProperty("客单价增长率")
    private BigDecimal cusPriceZzl;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("毛利率增长率")
    private BigDecimal profitZzl;

    @ApiModelProperty("连带率")
    private BigDecimal jointRate;

    @ApiModelProperty("连带率增长率")
    private BigDecimal jointRateZzl;

    @ApiModelProperty("分公司排名")
    private Integer topComp;

    @ApiModelProperty("全国排名")
    private Integer topTot;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("同期增长率")
    private BigDecimal yoySalesZzl;

    @ApiModelProperty("开单数量")
    private Integer billNum;

    @ApiModelProperty("上报时间")
    private Date etlTm;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}