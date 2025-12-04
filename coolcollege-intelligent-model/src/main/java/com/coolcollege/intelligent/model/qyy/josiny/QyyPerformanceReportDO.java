package com.coolcollege.intelligent.model.qyy.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyPerformanceReportDO implements Serializable {

    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("第三方唯一id")
    private String thirdDeptId;
    @ApiModelProperty("门店id")
    private String storeId;
    @ApiModelProperty("门店名称")
    private String storeName;
    @ApiModelProperty("区域id")
    private String regionId;
    @ApiModelProperty("区域路径")
    private String regionPath;
    @ApiModelProperty("时间类型： month:月 week:周 day:天")
    private String timeType;
    @ApiModelProperty("业务日期 月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    private String timeValue;
    @ApiModelProperty("创建人id")
    private String createUserId;
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @ApiModelProperty("修改人id")
    private String updateUserId;
    @ApiModelProperty("修改人名称")
    private String updateUserName;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("推送类型")
    private String pushType;
    @ApiModelProperty("总销售额")
    private BigDecimal grossSales;
    private String grossSalesString;
    @ApiModelProperty("总销售额同比")
    private BigDecimal grossSalesYoy;
    @ApiModelProperty("销售额达成率")
    private BigDecimal grossSalesRate;
    @ApiModelProperty("完成率")
    private BigDecimal finishRate;
    @ApiModelProperty("完成率同比")
    private BigDecimal finishRateYoy;
    @ApiModelProperty("单产")
    private BigDecimal output;
    @ApiModelProperty("单产同比")
    private BigDecimal outputYoy;
    @ApiModelProperty("单产达成率")
    private BigDecimal outputRate;
    @ApiModelProperty("缺口")
    private BigDecimal breach;
    @ApiModelProperty("销量")
    private BigDecimal salesVolume;
    @ApiModelProperty("销量达成率")
    private BigDecimal salesVolumeRate;
    @ApiModelProperty("客单价")
    private BigDecimal perCustomer;
    @ApiModelProperty("客单价达成率")
    private BigDecimal perCustomerRate;
    @ApiModelProperty("业绩同比")
    private BigDecimal achieveYoy;

    private String rankIcon;

    private String backgroundImage;

    private String subIcon;

    private String outputYoyIcon;

    private String achieveYoyIcon;
}
