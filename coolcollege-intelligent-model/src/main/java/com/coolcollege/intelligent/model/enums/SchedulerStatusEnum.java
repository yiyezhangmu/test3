package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SchedulerStatusEnum 枚举
 * @author 首亮
 */

public enum SchedulerStatusEnum {

  /**
   * 定时器状态
   */
  ON("on", "开启"),
  OFF("off", "停止"),
  ;

  private static final Map<String, SchedulerStatusEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(SchedulerStatusEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  SchedulerStatusEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static SchedulerStatusEnum getByCode(String code) {
    return map.get(code);
  }

}
