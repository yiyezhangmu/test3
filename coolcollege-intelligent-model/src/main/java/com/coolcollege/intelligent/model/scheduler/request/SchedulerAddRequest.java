package com.coolcollege.intelligent.model.scheduler.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * SchedulerAddRequest
 *
 * @author 首亮
 */
@Data
public class SchedulerAddRequest {

  /**
   * 调度任务类型（单词：once, 循环： period,calendar）
   */
  private String type;
  /**
   * 状态 on:开启；off：关闭
   */
  private String status;
  /**
   * 任务开始时间
   */
  @JSONField(name="start_time")
  private String startTime;
  /**
   * 任务结束时间
   */
  @JSONField(name="end_time",format = "yyyy-MM-dd")
  private Date endTime;
  /**
   * 回调地址列表
   */
  private List<ScheduleCallBackRequest> jobs;

  /**
   * 执行信息
   */
  @JSONField(name="calendar_info")
  private SchedulerCalendarInfoRequest calendarInfo;

  /**
   * 执行次数
   */
  @JSONField(name = "times")
  private Integer times;
  /**
   * 执行间隔
   */
  @JSONField(name = "interval")
  private Integer interval;
}
