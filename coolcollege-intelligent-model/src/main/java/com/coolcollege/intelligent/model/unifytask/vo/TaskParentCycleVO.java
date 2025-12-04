package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
    中间页 循环父任务 跳中间页
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskParentCycleVO {

    private Long unifyTaskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 创建人名称
     */
    private String createUserName;

    private String storeId;
    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 检查项个数
     */
    private Integer metaColumnCount;

    /**
     * 循环轮次
     */
    private Long loopCount;

    private String tableName;

    private String handleUserName;

    private String approveUserName;

    private String recheckUserName;

    private String ccUserName;

    private String status;
    // 是否逾期
    private Boolean overdueTask;
    // 同一批次中完成门店数量
    private Integer finishCount = 0;
    private Integer allCount = 0;

    private String storeRange;

    /**
     * 催办人员集合
     */
    private Set<String> urgingUser;
}
