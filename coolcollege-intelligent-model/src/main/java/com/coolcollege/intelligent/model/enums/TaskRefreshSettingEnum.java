package com.coolcollege.intelligent.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * describe: 巡店任务刷新设置
 *
 * @author wangff
 * @date 2025/1/8
 */
@Getter
@AllArgsConstructor
public enum TaskRefreshSettingEnum {
    NO_CLEAR("noClear", "不清空实际处理人"),

    ABSENT_CLEAR_HANDLER_USER("absentClearHandlerUser", "仅实际处理人不在当前处理人中时清空实际处理人"),
    ;
    private final String code;

    private final String msg;

}
