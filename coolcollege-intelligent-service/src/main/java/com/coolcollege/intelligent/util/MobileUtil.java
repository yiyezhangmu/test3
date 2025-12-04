package com.coolcollege.intelligent.util;

import cn.hutool.core.util.ObjectUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.enums.SmsZoneEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * describe: 手机号工具类
 *
 * @author wangff
 * @date 2024/11/8
 */
public class MobileUtil {

    /**
     * 根据手机号获取短信手机区号类型枚举
     * @param mobile 手机号
     * @return 短信手机区号类型枚举
     */
    public static String getSmsTemplateCode(String mobile) {
        SmsZoneEnum smsZoneEnum = SmsZoneEnum.getByMobile(mobile);
        if (ObjectUtil.isNull(smsZoneEnum)) {
            throw new ServiceException(ErrorCodeEnum.NONSUPPORT_MOBILE);
        }
        return smsZoneEnum.getTemplateCode();
    }

    /**
     * 去除手机号中的+号和空格
     * @param mobile 手机号
     * @return 手机号
     */
    public static String transNoPlusAndBlank(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return mobile;
        }
        String[] array = StringUtils.split(mobile, ' ');
        if (array.length < 2) {
            return mobile;
        }
        return array[0].substring(1).trim() + array[1].trim();
    }

    /**
     * 校验手机号格式
     * @param mobile 手机号
     * @return 格式是否正确
     */
    public static boolean validateMobile(String mobile) {
        SmsZoneEnum smsZoneEnum = SmsZoneEnum.getByMobile(mobile);
        return ObjectUtil.isNotNull(smsZoneEnum);
    }

    /**
     * 手机号统一存储格式
     * <p>
     *     国内手机号去除86/+86，国外手机号格式不变
     * </p>
     * @param mobile 手机号
     * @return 格式化后手机号
     */
    public static String unifyMobile(String mobile) {
        SmsZoneEnum smsZoneEnum = SmsZoneEnum.getByMobile(mobile);
        if (SmsZoneEnum.CHINA.equals(smsZoneEnum) && (mobile.startsWith("+86") || mobile.startsWith("86"))) {
            mobile = mobile.substring(mobile.indexOf("86") + 2).trim();
        }
        return mobile;
    }

}
