package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author wxp
 * @FileName: CombineTaskStoreDTO
 * @Description: 合并通知
 * @date 2023-11-22 19:57
 */
@Data
public class CombineTaskStoreDTO {
    /**
     * 门店任务列表
     */
    private PageInfo<TaskStoreInfo> pageInfo;
    /**
     * 任务信息
     */
    private TaskInfo taskInfo;

    @Data
    public static class TaskStoreInfo{

        @ApiModelProperty(value = "门店任务id")
        private Long taskStoreId;

        @ApiModelProperty(value = "父任务id")
        private Long unifyTaskId;

        @ApiModelProperty(value = "轮次")
        private Long loopCount;

        @ApiModelProperty(value = "子任务id")
        private Long subTaskId;

        @ApiModelProperty(value = "门店id")
        private String storeId;

        private String storeName;
        @ApiModelProperty(value = "门头照")
        private String avatar;

        @ApiModelProperty(value = "门店地址")
        private String storeAddress;
        /**
         * 门店状态（open：营业、closed：闭店、not_open：未开业）
         */
        @ApiModelProperty(value = "门店状态（open：营业、closed：闭店、not_open：未开业）")
        private String storeStatus;

        /**
         * 当前流程进度节点  任务状态
         */
        @ApiModelProperty(value = "当前节点 任务状态")
        private String nodeNo;

        /**
         * 任务处理时间
         */
        @ApiModelProperty(value = "处理时间")
        private Date handleTime;
        @ApiModelProperty(value = "子任务结束时间")
        private Date subEndTime;
        /**
         * 处理截止时间
         */
        @ApiModelProperty(value = "处理截止时间")
        private Date handlerEndTime;

        /**
         * 是否逾期
         */
        @ApiModelProperty(value = "是否逾期")
        private Boolean overdue;

        /**
         * 处理超时
         */
        @ApiModelProperty(value = "处理超时")
        private Boolean handlerOverdue;

        public Boolean getOverdue() {
            Date checkTime = new Date();
            // 已完成状态，判断逾期条件为任务的处理时间
            if (UnifyNodeEnum.END_NODE.getCode().equals(nodeNo)) {
                checkTime = handleTime;
            }
            return checkTime.after(subEndTime);
        }
    }

    @Data
    public static class TaskInfo{
        /**
         * 任务名称
         */
        @ApiModelProperty(value = "任务名称")
        private String taskName;
        /**
         * 任务类型
         */
        @ApiModelProperty(value = "任务类型")
        private String taskType;
        /**
         * 任务创建者
         */
        private String createUserId;
        /**
         * 创建人名称
         */
        @ApiModelProperty(value = "创建人名称")
        private String createUserName;

        /**
         * 处理截止时间
         */
        @ApiModelProperty(value = "处理截止时间")
        private Date handlerEndTime;

        @ApiModelProperty(value = "子任务结束时间")
        private Date subEndTime;
    }

}
