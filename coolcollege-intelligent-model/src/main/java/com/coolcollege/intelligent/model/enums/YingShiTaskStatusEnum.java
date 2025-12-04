package com.coolcollege.intelligent.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * describe: 萤石云任务状态 枚举类
 *
 * @author wangff
 * @date 2024/11/25
 */
@AllArgsConstructor
@Getter
public enum YingShiTaskStatusEnum {
    COMPLETE(0, "已完成"),
    WAITING(1, "排队中"),
    PROCESSING(2, "进行中"),
    FINISHED(3, "已结束"),
    EXCEPTION_FAILED(4, "异常结束"),
    EXCEPTION_PAUSE(5, "暂停中"),
    CANCEL(6, "已取消"),
    NOT_START(7, "未开始"),
    ;

    private final Integer code;

    private final String msg;
}
