package com.coolcollege.intelligent.common.enums.inspection;
import lombok.Getter;



/**
 * 工单创建规则枚举
 * 0-不自动发起
 * 1-自动发起
 * 2-自动发起(当前抓拍时段仅发起一次)
 * 3-自动发起(当天仅发起一次)
 * @author byd
 */
@Getter
public enum TicketCreateRuleEnum {

    /**
     * 不自动发起
     */
    NOT_AUTO(0, "不自动发起"),

    /**
     * 自动发起
     */
    AUTO(1, "自动发起"),

    /**
     * 自动发起(当前抓拍时段仅发起一次)
     */
    AUTO_ONCE_PER_PERIOD(2, "自动发起(当前抓拍时段仅发起一次)"),

    /**
     * 自动发起(当天仅发起一次)
     */
    AUTO_ONCE_PER_DAY(3, "自动发起(当天仅发起一次)");

    private final int code;
    private final String desc;

    TicketCreateRuleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static TicketCreateRuleEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TicketCreateRuleEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
