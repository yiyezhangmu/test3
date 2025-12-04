package com.coolcollege.intelligent.model.activity.entity;

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
 * @date   2024-09-02 06:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterStoreInfoDO implements Serializable {

    private Long id;

    @ApiModelProperty("促销员userId")
    private String promoterUserId;

    @ApiModelProperty("促销员名称")
    private String promoterName;

    @ApiModelProperty("促销员编号")
    private String promoterNum;

    @ApiModelProperty("促销员类型 full_time:全职 part_time:兼职")
    private String promoterType;

    @ApiModelProperty("商品品类")
    private String categoryName;

    @ApiModelProperty("品类代码")
    private String categoryCode;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店等级")
    private String storeLevel;

    @ApiModelProperty("门店类型 full_time:全职 part_time:兼职")
    private String storeType;

    @ApiModelProperty("物理门店编号")
    private String physicalStoreNum;

    @ApiModelProperty("门店离职状态 0.否 1.是")
    private String dimissionStatus;

    @ApiModelProperty("门店离职时间")
    private Date dimissionDate;

    @ApiModelProperty("门店重新入职时间")
    private Date reAdmissionDate;

    @ApiModelProperty("有无基本工资 Y ,N")
    private String withoutBasicPay;

    @ApiModelProperty("有无提成 Y ,N")
    private String withoutCommission;

    @ApiModelProperty("入社日期")
    private Date admissionDate;

    @ApiModelProperty("入店日期")
    private Date entryDate;

    @ApiModelProperty("试用期")
    private String probationPeriod;

    @ApiModelProperty("本月在职日期从")
    private Date jobOnMonthStartDate;

    @ApiModelProperty("本月在职日期到")
    private Date jobOnMonthEndDate;

    @ApiModelProperty("本月在职天数")
    private Integer jobOnMonthDay;

    @ApiModelProperty("中途换店")
    private String stopShop;

    @ApiModelProperty("本店在职日期从")
    private Date jobOnStoreMonthStartDate;

    @ApiModelProperty("本店在职日期到")
    private Date jobOnStoreMonthEndDate;

    @ApiModelProperty("本店在职天数")
    private Integer jobOnStoreMonthDay;

    @ApiModelProperty("当地工资标准")
    private BigDecimal localWageScale;

    @ApiModelProperty("当月基本工资")
    private BigDecimal basicMonthlyWage;

    @ApiModelProperty("工资比例")
    private BigDecimal wageRatio;

    @ApiModelProperty("基本工资金额")
    private BigDecimal basicWageAmount;

    @ApiModelProperty("工龄工资")
    private Integer seniorityWage;

    @ApiModelProperty("参保地代码")
    private String insuredPlaceCode;

    @ApiModelProperty("参保地名称")
    private String insuredPlaceName;

    @ApiModelProperty("最低工资所在地代码")
    private String minimumWagePlaceCode;

    @ApiModelProperty("最低工资所在地")
    private String minimumWagePlaceName;

    @ApiModelProperty("营业大区名称")
    private String businessRegionName;

    @ApiModelProperty("营业大区代码")
    private String businessRegionCode;

    @ApiModelProperty("营业分部名称")
    private String businessSegmentName;

    @ApiModelProperty("营业分部代码")
    private String businessSegmentCode;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("是否删除：0.否 1.是")
    private Boolean deleted;
}