package com.coolcollege.intelligent.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

    /**
     * 获取下一个时间的字符串格式
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static String getNextDateStr(Date date, int field, int amount) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 判断当前时间是否是周末
     *
     * @param date
     * @return
     */
    public static boolean isWeekDay(Date date) {
        boolean result = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayType = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayType == Calendar.SATURDAY || dayType == Calendar.SUNDAY) {
            result = true;
        }
        return result;
    }

    /**
     * 获取年月日  yyyyMMDD
     * @return
     */
    public static String getNowMonthDay(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd");
        return simpleDateFormat.format(date.getTime());
    }
}
