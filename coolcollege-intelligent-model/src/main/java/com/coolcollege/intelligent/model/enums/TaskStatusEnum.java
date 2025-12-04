package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskStatusEnum 枚举
 * @author 首亮
 */

public enum TaskStatusEnum {

  /**
   * 任务状态
   */
  COMPLETE("COMPLETE", "完成"),
  ONGOING("ONGOING", "进行中"),
  NOT_START("NOT_START", "未开始"),
  EXPIRE("EXPIRE", "过期"),
  DELETE("DELETE","已删除"),
    STOP("STOP","已停止")
  ;

  private static final Map<String, TaskStatusEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(TaskStatusEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  TaskStatusEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static TaskStatusEnum getByCode(String code) {
    return map.get(code);
  }

}
