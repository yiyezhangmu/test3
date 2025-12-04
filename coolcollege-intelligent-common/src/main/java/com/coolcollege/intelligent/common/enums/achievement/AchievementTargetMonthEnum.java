package com.coolcollege.intelligent.common.enums.achievement;

/**
 * @author chenyupeng
 * @since 2021/12/8
 */
public enum AchievementTargetMonthEnum {

    JANUARY(1,"-01-01","1月"),
    FEBRUARY(2,"-02-01","2月"),
    MARCH(3,"-03-01","3月"),
    APRIL(4,"-04-01","4月"),
    MAY(5,"-05-01","5月"),
    JUNE(6,"-06-01","6月"),
    JULY(7,"-07-01","7月"),
    AUGUST(8,"-08-01","8月"),
    SEPTEMBER(9,"-09-01","9月"),
    OCTOBER(10,"-10-01","10月"),
    NOVEMBER(11,"-11-01","11月"),
    DECEMBER(12,"-12-01","12月"),
    ;

    private Integer code;
    private String msg;
    private String dec;

    AchievementTargetMonthEnum(Integer code, String msg,String dec){
        this.code=code;
        this.msg=msg;
        this.dec=dec;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
