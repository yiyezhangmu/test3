package com.coolcollege.intelligent.common.util;

/**
 * @author zhangchenbiao
 * @FileName: SmsCodeUtil
 * @Description:验证码
 * @date 2021-07-21 11:27
 */
public class SmsCodeUtil {

    public static String getRandNum() {
        return String.valueOf((int)((Math.random()*9+1)*Math.pow(10,5)));
    }


}
