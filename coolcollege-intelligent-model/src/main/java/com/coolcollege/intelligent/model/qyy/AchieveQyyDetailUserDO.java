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
public class AchieveQyyDetailUserDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("时间类型：year:年 month:月 week:周 day:天")
    private String timeType;

    @ApiModelProperty("业务日期 年yyyy, 月yyyymm 周取周一对应yyyymmdd, 日yyyymmdd")
    private String timeValue;

    @ApiModelProperty("业绩目标")
    private BigDecimal goalAmt;

    @ApiModelProperty("完成业绩")
    private BigDecimal salesAmt;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("分公司排名")
    private Integer topComp;

    @ApiModelProperty("上报时间")
    private Date etlTm;

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

    public static AchieveQyyDetailUserDO convert(String storeId, String userId, TimeCycleEnum timeCycle, String timeValue, BigDecimal goalAmt, String operateUserId, String operateUserName){
        AchieveQyyDetailUserDO insert = new AchieveQyyDetailUserDO();
        insert.setStoreId(storeId);
        insert.setUserId(userId);
        insert.setTimeType(timeCycle.getCode());
        insert.setTimeValue(timeValue);
        insert.setGoalAmt(goalAmt);
        insert.setCreateUserId(operateUserId);
        insert.setCreateUserName(operateUserName);
        insert.setUpdateUserId(operateUserId);
        insert.setUpdateUserName(operateUserName);
        return insert;
    }

}