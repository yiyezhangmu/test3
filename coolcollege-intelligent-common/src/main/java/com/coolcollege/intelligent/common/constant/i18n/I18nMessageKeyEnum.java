package com.coolcollege.intelligent.common.constant.i18n;

import com.coolcollege.intelligent.common.util.I18nUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息国际化的key值枚举
 * 对应关系请到配置文件自查
 *
 * @author 邵凌志
 */
public enum I18nMessageKeyEnum {
    COOLCOLLEGE("coolcollege"),
    TASK("task"),
    PATROL_STORE_TASK("patrol.store.task"), // 巡店任务
    COMPLETE_STORE("complete.store"),   // 门店补全
    QUESTION_ORDER("question.order"),   // 问题工单
    TASK_REJECT("task.reject"), // 任务被驳回
    TASK_HANDLE("task.handle"), // 处理任务
    SCHEDULE_PATROL_TASK_REMIND("schedule.patrol.task.remind"), // 定時提醒进行巡店
    COMPLETE_TASK("complete.task"), // 完成任务
    TRANSMIT_TASK("transmit.task"), // 转发任务 add by lizhuo
    TRANSMIT_TASK2("transmit.task2"), // 转发任务 add by lizhuo
    DISPLAY_TASK_HANDLE("display.task.handle"), // 陈列任务 待处理 add by lizhuo
    DISPLAY_TASK_RECHECK("display.task.recheck"), // 陈列任务 待审核 add by lizhuo
    DISPLAY_TASK_APPROVE("display.task.approve"), // 陈列任务 待复核 add by lizhuo
    DISPLAY_TASK_COMPLETE("display.task.complete"), // 陈列任务 完成 add by lizhuo
    TRANSMIT_QUESTION_ORDER("transmit.question.order"),
    TASK_CC("task.cc"),
    CC_PS("task.cc.ps"),
    PATROL_STORE_PLAN("patrol.store.plan"),
    TASK_HANDLE_COMBINE("task.handle.combine"),
    ACHIEVEMENT_TASK_REMIND("achievement.task.remind"),
    CC_ONLINE("task.cc.online"),

    ;

    private static final Map<String, I18nMessageKeyEnum> map = Arrays.stream(values()).collect(Collectors.toMap(I18nMessageKeyEnum::getValue, Function.identity()));

    private String code;

    private String value;

    private String content;

    I18nMessageKeyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static I18nMessageKeyEnum parseValue(String value) {
        return map.get(value);
    }

    public String getContent() {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        ResourceBundle bundle = I18nUtil.getResourceBundle(I18nUtil.getLocaleByLang("zh_cn"));
        return bundle.getString(value);
    }
}
