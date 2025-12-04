package com.coolcollege.intelligent.model.scheduler.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.coolcollege.intelligent.model.enums.SchedulerStatusEnum;
import com.coolcollege.intelligent.model.enums.SchedulerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/7/16 11:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleFixedRequest {

    /**
     * 间隔时长（秒）， 默认一天
     */
    private int interval = 24 * 60 * 60;

    /**
     * 定时任务类型，默认循环执行
     */
    private String type = SchedulerTypeEnum.LOOP.getCode();

    /**
     * 开始执行时间
     */
    @JSONField(name = "start_time")
    private String startTime;

    /**
     * 定时器状态,默认开启
     */
    private String status = SchedulerStatusEnum.ON.getCode();

    /**
     * 执行次数，如果要一直执行，则把值设置足够大
     *
     * 默认int最大值
     */
    private int times = Integer.MAX_VALUE;

    /**
     * 回调地址列表
     */
    private List<ScheduleCallBackRequest> jobs;

    public ScheduleFixedRequest(String startTime, List<ScheduleCallBackRequest> jobs) {
        this.startTime = startTime;
        this.jobs = jobs;
    }
}
