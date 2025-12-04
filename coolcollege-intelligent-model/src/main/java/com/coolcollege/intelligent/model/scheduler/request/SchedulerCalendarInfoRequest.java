package com.coolcollege.intelligent.model.scheduler.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * SchedulerCalendarInfoRequest
 *
 * @author 首亮
 */
@Data
public class SchedulerCalendarInfoRequest {

  /**
   * 执行时间
   */
  @JSONField(name="calendar_time")
  private String calendarTime;
  /**
   * 周，月
   */
  @JSONField(name="calendar_type")
  private String calendarType;
  /**
   * 执行时间（周：1-7，月1-31逗号分隔）
   */
  @JSONField(name="calendar_value")
  private String calendarValue;


}
