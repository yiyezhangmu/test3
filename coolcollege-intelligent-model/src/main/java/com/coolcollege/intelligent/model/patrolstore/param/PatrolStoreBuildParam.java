package com.coolcollege.intelligent.model.patrolstore.param;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 巡店父任务初始化参数
 * 
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreBuildParam {
    /**
     * 父任务id
     */
    @NotNull(message = "父任务id不能为空")
    private Long unifyTaskId;


    private String taskName;

    /**
     * 巡店类型
     */
    @NotNull(message = "巡店类型不能为空")
    private String patrolType;

    /**
     * 创建人id
     */
    @NotNull(message = "创建人id不能为空")
    private String createUserId;

    /**
     * 检查表元数据id
     */
    @NotEmpty(message = "检查表不能为空")
    private List<Long> metaTableIds;

    /**
     * 检查表元数据id
     */
    @Deprecated
    private Long metaTableId;

    /**
     * 巡店设置
     */
    private EnterpriseStoreCheckSettingDO storeCheckSettingDO;

    private String taskInfo;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 子任务信息
     */
    @NotNull(message = "子任务信息不能为空")
    private PatrolStoreSubBuildParam subBuildParams;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatrolStoreSubBuildParam {
        /**
         * 子任务id
         */
        @NotNull(message = "子任务id不能为空")
        private Long subTaskId;

        /**
         * 门店id
         */
        @NotNull(message = "门店id不能为空")
        private String storeId;

        /**
         * 处理人id
         */
        @NotNull(message = "处理人id不能为空")
        private String handleUserId;

        /**
         * 循环任务循环轮次
         */
        private Long loopCount;


        /**
         * 子任务审批链开始时间
         */
        private Long subBeginTime;

        /**
         * 子任务审批链结束时间
         */
        private Long subEndTime;
    }
}
