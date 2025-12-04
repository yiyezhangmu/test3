package com.coolcollege.intelligent.model.enums;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.PhoneUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * describe: 短信手机区号类型
 *
 * @author wangff
 * @date 2024/11/8
 */
@Getter
@AllArgsConstructor
public enum SmsZoneEnum {
    INDONESIA("+62", "印度尼西亚", SmsCodeTypeEnum.LOGIN_INTERNATIONAL.getTemplateCode(), Pattern.compile("\\d{10,12}")),

    MALAYSIA("+60", "马来西亚", SmsCodeTypeEnum.LOGIN_INTERNATIONAL.getTemplateCode(), Pattern.compile("\\d{6,12}")),

    CHINA("+86", "中国", SmsCodeTypeEnum.LOGIN2.getTemplateCode(), PatternPool.MOBILE),
    ;

    /**
     * 区号
     */
    private final String code;

    /**
     * 描述
     */
    private final String msg;

    /**
     * 短信模板code
     */
    private final String templateCode;
    
    /**
     * 手机号格式
     */
    private final Pattern mobilePattern;

    /**
     * 根据手机号获取枚举类型
     * @param mobile 手机号
     * @return 短信手机区号类型
     */
    public static SmsZoneEnum getByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        if (PhoneUtil.isMobile(mobile)) {
            return CHINA;
        }
        String[] split = mobile.split(" ");
        if (split.length < 2) {
            return null;
        }
        return ArrayUtil.firstMatch(v -> split[0].startsWith(v.getCode()) && Validator.isMatchRegex(v.getMobilePattern(), split[1]), values());
    }
}
