package com.coolcollege.intelligent.common.enums.device;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 云视通（中维）消息推送类型枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/7/29
 */
@Getter
@RequiredArgsConstructor
public enum YunshitongMsgTypeEnum {
    ONLINE("device_login_message", "设备上线消息"),

    OFFLINE("device_logout_message", "设备下线消息")

    ;
    /**
     * 编码
     */
    private final String code;

    /**
     * 消息类型
     */
    private final String msg;

    public static YunshitongMsgTypeEnum getByCode(String code) {
        for (YunshitongMsgTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
