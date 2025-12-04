package com.coolcollege.intelligent.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 萤石存储设备状态枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/8/15
 */
@Getter
@AllArgsConstructor
public enum YingshiStorageStatusEnum {

    STATUS0("0", "正常"),
    STATUS1("1", "存储介质错误"),
    STATUS2("2", "未格式化"),
    STATUS3("3", "正在格式化"),
    ;

    private final String code;

    private final String msg;

    public static String getMsgByCode(String code) {
        for (YingshiStorageStatusEnum value : YingshiStorageStatusEnum.values()) {
            if (value.code.equals(code)) {
                return value.msg;
            }
        }
        return null;
    }
}
