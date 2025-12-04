package com.coolcollege.intelligent.model.qyy;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
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
public class AchieveQyyDetailStoreDO implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("第三方唯一id")
    private String thirdDeptId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("时间类型：year:年 month:月 week:周 day:天")
    private String timeType;

    @ApiModelProperty("业务日期 年yyyy, 月yyyymm 周取周一对应yyyymmdd, 日yyyymmdd")
    private String timeValue;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("业绩目标")
    private BigDecimal goalAmt;

    @ApiModelProperty("已分配业绩")
    private BigDecimal assignedGoalAmt;

    @ApiModelProperty("完成业绩")
    private BigDecimal salesAmt;

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

    public static AchieveQyyDetailStoreDO getDefaultDO(String storeId, TimeCycleEnum timeType, String timeValue){
        AchieveQyyDetailStoreDO defaultDO = new AchieveQyyDetailStoreDO();
        //defaultDO.setThirdDeptId();
        defaultDO.setStoreId(storeId);
        //defaultDO.setStoreName();
        //defaultDO.setRegionId();
        //defaultDO.setRegionPath();
        defaultDO.setTimeType(timeType.getCode());
        defaultDO.setTimeValue(timeValue);
        defaultDO.setSalesRate(BigDecimal.ZERO);
        defaultDO.setGoalAmt(BigDecimal.ZERO);
        defaultDO.setAssignedGoalAmt(BigDecimal.ZERO);
        defaultDO.setSalesAmt(BigDecimal.ZERO);
        return defaultDO;
    }

    public static AchieveQyyDetailStoreDO convert(RegionDO region, TimeCycleEnum timeCycle, String timeValue, BigDecimal goalAmt, BigDecimal assignedGoalAmt, String operateUserId, String operateUserName){
        AchieveQyyDetailStoreDO result = new AchieveQyyDetailStoreDO();
        result.setThirdDeptId(region.getThirdDeptId());
        result.setStoreId(region.getStoreId());
        result.setStoreName(region.getName());
        result.setRegionId(region.getId());
        result.setRegionPath(region.getRegionPath());
        result.setTimeType(timeCycle.getCode());
        result.setTimeValue(timeValue);
        result.setGoalAmt(goalAmt);
        result.setAssignedGoalAmt(assignedGoalAmt);
        result.setCreateUserId(operateUserId);
        result.setCreateUserName(operateUserName);
        result.setUpdateUserId(operateUserId);
        result.setUpdateUserName(operateUserName);
        return result;
    }
}