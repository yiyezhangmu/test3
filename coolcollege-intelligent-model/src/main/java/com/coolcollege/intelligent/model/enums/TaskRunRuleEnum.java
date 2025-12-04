package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskRunRuleEnum 枚举
 * @author 首亮
 */

public enum TaskRunRuleEnum {

  /**
   * 任务规则
   */
  ONCE("ONCE", "单次"),
  LOOP("LOOP", "循环"),
  ;

  private static final Map<String, TaskRunRuleEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(TaskRunRuleEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  TaskRunRuleEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static TaskRunRuleEnum getByCode(String code) {
    return map.get(code);
  }

}
