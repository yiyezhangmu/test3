package com.coolcollege.intelligent.model.patrolstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-09-03 11:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreCheckDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long taskId;

    @ApiModelProperty("稽核记录id")
    private Long businessId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径(新)")
    private String regionWay;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店人工号")
    private String supervisorJobNum;

    @ApiModelProperty("巡店开始时间")
    private Date signStartTime;

    @ApiModelProperty("巡店结束时间")
    private Date signEndTime;

    @ApiModelProperty("巡店类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    private String patrolType;

    @ApiModelProperty("多个检查表的ID")
    private String metaTableIds;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("循环任务的循环批次")
    private Long loopCount;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("子任务审批链开始时间")
    private Date subBeginTime;

    @ApiModelProperty("子任务审批链结束时间")
    private Date subEndTime;

    @ApiModelProperty("复审的巡店记录id")
    private Long recheckBusinessId;

    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核状态 0: 待稽核 1:已稽核")
    private Integer bigRegionCheckStatus;

    @ApiModelProperty("大区稽核时间")
    private Date bigRegionCheckTime;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    private Integer warZoneCheckStatus;

    @ApiModelProperty("战区稽核时间")
    private Date warZoneCheckTime;
}