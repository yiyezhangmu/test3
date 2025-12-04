package com.coolcollege.intelligent.model.enums;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 用户权限来源
 *
 * @ClassName: UserAuthMappingSourceEnum
 * @Author: xugangkun
 * @Date: 2021/3/30 11:43
 */
public enum UserAuthMappingSourceEnum {
    /**
     * 酷店掌创建
     */
    CREATE("create", "酷店掌创建"),
    /**
     * 钉钉同步
     */
    SYNC("sync", "钉钉同步");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String msg;

    UserAuthMappingSourceEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(String code) {
        if (StringUtils.isEmpty(code) || code == null) {
            return "";
        }
        for (UserAuthMappingSourceEnum sourceEnum : UserAuthMappingSourceEnum.values()) {
            if (code.equals(sourceEnum.getCode())) {
                return sourceEnum.getMsg();
            }
        }
        return "";
    }

    public static String getCodeByMsg(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return "";
        }
        for (UserAuthMappingSourceEnum sourceEnum : UserAuthMappingSourceEnum.values()) {
            if (msg.equals(sourceEnum.getMsg())) {
                return sourceEnum.getCode();
            }
        }
        return "";
    }
}
