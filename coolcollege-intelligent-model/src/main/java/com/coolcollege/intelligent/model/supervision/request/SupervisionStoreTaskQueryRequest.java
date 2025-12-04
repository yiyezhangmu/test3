package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 16:09
 * @Version 1.0
 */
@Data
public class SupervisionStoreTaskQueryRequest {

    @ApiModelProperty(value = "督导用户ID")
    private String supervisionUserId;
    @ApiModelProperty(value = "门店ID")
    private String storeId;

    @ApiModelProperty(value = "TODO-待完成 COMPLETE-按时完成 OVERDUE-逾期未完成 OVERDUE_COMPLETION-逾期完成 APPROVAL-待审批", required = true)
    private SupervisionSubTaskStatusEnum statusEnum;

//    @ApiModelProperty(value = "截止时间区间_开始")
//    private Long startTime;
//
//    @ApiModelProperty(value = "截止时间区间_结束")
//    private Long endTime;
//
//    @ApiModelProperty(value = "截止时间是否升序 true 是  false 降序")
//    private Boolean ascFlag;
//
//    @ApiModelProperty(value = "任务优先级")
//    private SupervisionTaskPriorityEnum taskPriorityEnum;

    @ApiModelProperty(value = "第几页")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "分页大小")
    private Integer pageSize = 10;
    @ApiModelProperty(value = "是否逾期")
    private Integer handleOverTimeStatus;
}
