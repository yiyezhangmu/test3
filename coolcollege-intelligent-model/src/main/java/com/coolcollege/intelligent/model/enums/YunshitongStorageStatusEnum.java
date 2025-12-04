package com.coolcollege.intelligent.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 云视通设备状态枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/8/13
 */
@Getter
@AllArgsConstructor
public enum YunshitongStorageStatusEnum {

    NORMAL("normal", "正常"),
    UNFORMATED("unformated", "未格式化"),
    FORMATING("formating", "正在格式化"),
    BREAKDOWN("breakdown", "损坏"),
    EX_OTHERDATA("ex_otherdata", "存在其他文件"),
    READONLY("readonly", "SD卡只读"),
    TYPE_UNSUPPORTED("type_unsupported", "SD卡类型不支持"),
    ;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String msg;

    public static String getMsgByStatus(String status) {
        for (YunshitongStorageStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value.msg;
            }
        }
        return null;
    }
}
