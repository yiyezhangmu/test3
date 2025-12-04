package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskRunRuleEnum 枚举
 * @author 首亮
 */

public enum TaskCycleEnum {

  /**
   * 任务周期
   */
  QUARTER("QUARTER", "季"),
  MONTH("MONTH", "月"),
  WEEK("WEEK", "周"),
  DAY("DAY", "日"),
  HOUR("HOUR", "小时"),
  ;

  private static final Map<String, TaskCycleEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(TaskCycleEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  TaskCycleEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static TaskCycleEnum getByCode(String code) {
    return map.get(code);
  }

}
