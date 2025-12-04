package com.coolcollege.intelligent.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 隐秘数据工具类
 */
@Slf4j
public class HideDataUtil {

    /**
     * 隐藏银行卡号中间的字符串（使用*号），显示前四后四
     * @param cardNo
     * @return String
     */
    public static String hideCardNo(String cardNo) {
        if(StringUtils.isBlank(cardNo)) {
            return cardNo;
        }
        return cardNo.replaceAll("(\\d{4})(\\d{11})(\\d{4})", "$1****$3");
    }


    /**
     * 隐藏手机号中间位置字符，显示前三后三个字符
     * @param phoneNo
     * @return
     */
    public static String hidePhoneNo(String phoneNo) {
        if(StringUtils.isBlank(phoneNo)) {
            return phoneNo;
        }
        try {
            if(isMobile(phoneNo)){
                return phoneNo.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
        } catch (Exception e) {
            log.info("手机号格式不正确"+e);
            return phoneNo;
        }
        try {
            if(isFixedPhone(phoneNo)){
                return phoneNo.replaceAll("^(.{3})(.*)(.{4})$", "$1****$2");
            }
        } catch (Exception e) {
            log.info("电话号码格式不正确"+e);
            return phoneNo;
        }
        return phoneNo;
    }


    /**
     * 手机号码校验
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile){
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 电话号码校验
     * @param fixedPhone
     * @return
     */
    public static boolean isFixedPhone(String fixedPhone){
        String reg="(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        return Pattern.matches(reg, fixedPhone);
    }


}
