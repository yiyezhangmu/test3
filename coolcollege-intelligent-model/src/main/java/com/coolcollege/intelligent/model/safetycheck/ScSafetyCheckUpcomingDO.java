package com.coolcollege.intelligent.model.safetycheck;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScSafetyCheckUpcomingDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("处理人id")
    private String userId;

    @ApiModelProperty("节点1,2,3,4  appealApprove:申诉审核")
    private String nodeNo;

    @ApiModelProperty("状态 ongoing进行中,completed已完成")
    private String status;

    @ApiModelProperty("审批轮次")
    private Integer cycleCount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("门店id")
    private String storeId;
}