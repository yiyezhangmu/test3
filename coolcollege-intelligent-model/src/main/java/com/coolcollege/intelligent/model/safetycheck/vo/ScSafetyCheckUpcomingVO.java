package com.coolcollege.intelligent.model.safetycheck.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2023-08-17 14:28
 */
@ApiModel
@Data
public class ScSafetyCheckUpcomingVO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("处理人id")
    private String userId;

    @ApiModelProperty("节点1,2,3,4  1：待处理 其他:待审批")
    private String nodeNo;

    @ApiModelProperty("状态 ongoing进行中,completed已完成")
    private String status;

    @ApiModelProperty("审批轮次")
    private Integer cycleCount;

    @ApiModelProperty("签退时间(提交时间)")
    private Date signEndTime;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人名称")
    private String supervisorName;
}
