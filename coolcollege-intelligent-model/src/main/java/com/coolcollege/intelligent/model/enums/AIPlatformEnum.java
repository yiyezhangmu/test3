package com.coolcollege.intelligent.model.enums;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * AI平台枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/8/1
 */
@Getter
@AllArgsConstructor
public enum AIPlatformEnum {

    BAILIAN("bailian", "百炼大模型", "bailianAIOpenServiceImpl"),
    HLS("hls", "华莱士AI", "hlsAIOpenServiceImpl"),
    AI_HUB("ai_hub", "AIHUB", "aIHubAIOpenServiceImpl"),
    HUOSHAN("huoshan", "火山引擎", "huoshanAIOpenServiceImpl"),
    YINGSHI("yingshi", "萤石", "yingshiAIOpenServiceImpl"),
    HIKVISION("hikvision", "海康威视", "hikvisionAIOpenServiceImpl"),
    SHUZHIMALI("shuzimali", "数字码力", "shuzimaliAIOpenServiceImpl"),
    ;

    private final String code;

    private final String msg;

    private final String beanName;

    public static AIPlatformEnum getByCode(String code) {
        for (AIPlatformEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new ServiceException(ErrorCodeEnum.AI_PLATFORM_NOT_EXIST);
    }
}
