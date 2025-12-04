package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SchedulerTypeEnum 枚举
 * @author 首亮
 */

public enum SchedulerTypeEnum {

  /**
   * 定时器类型
   */
  CALENDAR("calendar", "日历"),
  ONCE("once", "单次"),
  LOOP("period", "循环"),
  ;

  private static final Map<String, SchedulerTypeEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(SchedulerTypeEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  SchedulerTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static SchedulerTypeEnum getByCode(String code) {
    return map.get(code);
  }

}
