package com.coolcollege.intelligent.model.enums;


/**
 * ScheduleCallBackEnum
 *
 * @author 首亮
 */

public enum ScheduleCallBackEnum {
  /**
   * 回调方式
   */
  api("api"),

  script("script"),

  mq("mq");

  private final String value;

  ScheduleCallBackEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
