package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按人任务taskInfoDTO
 * @author zhangnan
 * @date 2022-04-14 15:46
 */
@Data
public class TaskPersonTaskInfoDTO {

    @ApiModelProperty(value = "执行要求")
    private ExecuteDemand executeDemand;

    @ApiModelProperty(value = "执行方式")
    private ExecuteWay executeWay;

    private PatrolParam patrolParam;

    @Data
    public static class PatrolParam {
        @ApiModelProperty(value = "执行要求")
        private ExecuteDemand patrolParam;
    }

    @Data
    public static class ExecuteDemand {
        @ApiModelProperty(value = "巡店数量")
        private Integer patrolStoreNum;

        @ApiModelProperty(value = "是否去重")
        private Boolean isDistinct;

        @ApiModelProperty(value = "巡检门店要求all/auth")
        private String storeRange;
    }

    @Data
    public static class ExecuteWay {
        @ApiModelProperty(value = "执行方式：PATROL_STORE_OFFLINE（线下巡店）")
        private String way;

        @ApiModelProperty(value = "巡店总结")
        private Boolean isOpenAutograph;

        @ApiModelProperty(value = "巡店签名")
        private Boolean isOpenSummary;
    }
}
