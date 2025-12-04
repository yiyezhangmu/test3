package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * QuestionActionKeyEnum 枚举
 * @author 首亮
 */

public enum QuestionActionKeyEnum {

  /**
   * 工单处理action_key
   */
  PENDING("pending", "待处理"),
  NO_QUESTION("no_question", "无问题维持现状"),
  REVIEW("review", "已处理请复检"),
  RE_DO("re_do", "重新处理"),
  PASS("pass", "完结"),
  TURN("turn", "转发");
  ;

  private static final Map<String, QuestionActionKeyEnum> map = Arrays.stream(values()).collect(
      Collectors.toMap(QuestionActionKeyEnum::getCode, Function.identity()));


  private String code;
  private String desc;

  QuestionActionKeyEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static QuestionActionKeyEnum getByCode(String code) {
    return map.get(code);
  }

}
