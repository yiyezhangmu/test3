package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskTypeEnum 枚举
 * @author 首亮
 */

public enum TaskTypeEnum {

  /**
   * 任务类型
   */
  PATROL_STORE_ONLINE("PATROL_STORE_ONLINE", "视频巡店"),
  PATROL_STORE_OFFLINE("PATROL_STORE_OFFLINE", "线下巡店"),
  PATROL_STORE_PICTURE_ONLINE("PATROL_STORE_PICTURE_ONLINE", "定时巡检"),
  PATROL_STORE_INFORMATION("PATROL_STORE_INFORMATION", "信息补全"),
  PATROL_STORE_AI("PATROL_STORE_AI", "AI巡检"),
  STORE_SELF_CHECK("STORE_SELF_CHECK", "交叉巡店"),
  QUESTION_ORDER("QUESTION_ORDER", "问题工单"),
  DISPLAY_TASK("DISPLAY_TASK", "陈列任务"),
  TB_DISPLAY_TASK("TB_DISPLAY_TASK", "新陈列任务"),
  PATROL_STORE_PLAN("PATROL_STORE_PLAN", "计划巡店"),
  PATROL_STORE_FORM("PATROL_STORE_FORM", "表单巡店"),
  SUPERVISION("SUPERVISION", "督导助手"),
  PRODUCT_FEEDBACK("PRODUCT_FEEDBACK","货品反馈"),
  PATROL_STORE_SAFETY_CHECK("PATROL_STORE_SAFETY_CHECK", "稽核巡店"),//食安稽核
  PATROL_STORE_MYSTERIOUS_GUEST("PATROL_STORE_MYSTERIOUS_GUEST", "神秘访客任务"),
  SELF_PATROL_STORE("SELF_PATROL_STORE","自主巡店"),

  ACHIEVEMENT_NEW_RELEASE("ACHIEVEMENT_NEW_RELEASE", "新品上架"),
  ACHIEVEMENT_OLD_PRODUCTS_OFF("ACHIEVEMENT_OLD_PRODUCTS_OFF","老品下架"),
  ;

  public static final Map<String, TaskTypeEnum> map = Arrays.stream(values()).collect(
          Collectors.toMap(TaskTypeEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  TaskTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static TaskTypeEnum getByCode(String code) {
    return map.get(code);
  }

  public static String getDescByCode(String code) {
    TaskTypeEnum taskTypeEnum = map.get(code);
    return Optional.ofNullable(taskTypeEnum).map(TaskTypeEnum::getDesc).orElse("");
  }

  public static boolean isTaskTypeFilterStoreStatus(String taskType){
    return (TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(taskType)
            || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(taskType)
            || TaskTypeEnum.PRODUCT_FEEDBACK.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(taskType));
  }

  public static boolean isNeedDevice(String taskType){
    return TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskType) || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(taskType);
  }

  /**
   * 可以发送给合并通知待办消息的任务类型
   */
  public static boolean isCombineNoticeTypes(String taskType) {
    return TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(taskType)
            || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(taskType)
            || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType);
  }

  public static String getTaskTypeByCode(String code) {
    TaskTypeEnum taskTypeEnum = map.get(code);
    if (Objects.nonNull(taskTypeEnum)) {
      if (taskTypeEnum.equals(TB_DISPLAY_TASK)) {
        return "陈列任务";
      } else {
        return taskTypeEnum.getDesc() + (taskTypeEnum.getDesc().endsWith("任务") ? "" : "任务");
      }
    }
    return "";
  }
}
