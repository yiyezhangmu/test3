package com.coolcollege.intelligent.model.qyy.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyTargetDO implements Serializable {

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
    @ApiModelProperty("业绩目标")
    private BigDecimal goalAmt;
    @ApiModelProperty("单产目标")
    private BigDecimal unitYieldTarget;
    @ApiModelProperty("销量目标")
    private BigDecimal salesTarget;
    @ApiModelProperty("HQ:总部,COMP：分公司,SUP：督导，STORE:门店")
    private String pushType;
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


}
