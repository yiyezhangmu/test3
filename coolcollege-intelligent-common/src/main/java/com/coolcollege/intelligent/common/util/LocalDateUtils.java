package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: LocalDateUtils
 * @Description: local工具类
 * @date 2023-04-26 11:18
 */
public class LocalDateUtils {


    public static String getYYYYMM(LocalDate localDate){
        int monthValue = localDate.getMonthValue();
        if(monthValue < Constants.TEN){
            return localDate.getYear() + "-0" + monthValue;
        }
        return localDate.getYear() + "-" + monthValue;
    }


    /**
     * 或某月的所有日期
     * @param localDate
     * @return
     */
    public static List<String> getDaysOfMonth(LocalDate localDate){
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        // 获取该月份的第一天和最后一天日期
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        // 遍历该月份的每一天，并将其格式化为 "yyyy-MM-dd" 输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> daysOfMonth = new ArrayList<>();
        LocalDate currentDay = firstDay;
        while (!currentDay.isAfter(lastDay)) {
            String formattedDay = currentDay.format(formatter);
            daysOfMonth.add(formattedDay);
            currentDay = currentDay.plusDays(1);
        }
        return daysOfMonth;
    }

    /**
     * 获取一周的所有天
     * @param localDate
     * @return
     */
    public static List<String> getDaysOfWeek(LocalDate localDate){
        // 获取该月份的第一天和最后一天日期
        LocalDate firstDay = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 遍历该月份的每一天，并将其格式化为 "yyyy-MM-dd" 输出
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add(firstDay.toString());
        for (int i = 1; i < Constants.SEVEN ; i++) {
            daysOfWeek.add(firstDay.plusDays(i).toString());
        }
        return daysOfWeek;
    }


    /**
     * 时间格式转换成LocalDate
     * @param dateString
     * @return
     */
    public static LocalDate dateConvertLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            throw new ServiceException(ErrorCodeEnum.DATE_NULL);
        }
        try {
            // yyyy 格式
            if (dateString.length() == 4) {
                return LocalDate.parse(dateString + "-01-01");
            }
            // yyyy-MM 格式
            if (dateString.length() == 7) {
                return LocalDate.parse(dateString + "-01");
            }
            // yyyy-MM-dd 格式
            if (dateString.length() == 10) {
                return LocalDate.parse(dateString);
            } else {
                throw new ServiceException(ErrorCodeEnum.DATE_STYLE_ERROR);
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.DATE_STYLE_ERROR);
        }
    }

}
