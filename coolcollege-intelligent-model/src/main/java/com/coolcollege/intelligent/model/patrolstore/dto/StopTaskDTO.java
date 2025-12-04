package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StopTaskDTO {

    @ApiModelProperty("父任务主键ID")
    private Long parentTaskId;
//    @NotNull(message = "任务类型不能为空")
//    @ApiModelProperty("任务类型：1.PATROL_STORE_OFFLINE(线下巡店) " +
//            "2.PATROL_STORE_ONLINE（视频巡店） " +
//            "3.PATROL_STORE_PICTURE_ONLINE（定时巡检） " +
//            "4.DISPLAY_TASK（陈列任务）")
//    private String workType;
}
