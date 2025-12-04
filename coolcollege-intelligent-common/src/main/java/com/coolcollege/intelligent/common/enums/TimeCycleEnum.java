package com.coolcollege.intelligent.common.enums;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import javafx.util.Pair;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: BusinessCycleEnum
 * @Description: 执行类型枚举
 * @date 2022-06-17 9:42
 */
public enum TimeCycleEnum {

    //执行类型：year:年 month:月 week:周 day:天
    YEAR("year", "年"),
    MONTH("month", "月"),
    WEEK("week", "周"),
    DAY("day", "天");

    private final String code;

    private final String message;

    TimeCycleEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static TimeCycleEnum getTimeCycleEnumByCode(String code){
        for (TimeCycleEnum value : TimeCycleEnum.values()) {
            if(value.name().equals(code)){
                return value;
            }
        }
        return null;
    }

    public static Pair<String, String> getStartAndEndTime(TimeCycleEnum timeCycle, Integer timeUnion){
        if(Objects.isNull(timeCycle) || Objects.isNull(timeUnion)){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.now(), endDate = LocalDate.now();
        String startDateTime = null;
        String endDateTime = null;
        try {
            if(YEAR.equals(timeCycle)){
                startDate = LocalDate.of(timeUnion, Constants.INDEX_ONE, Constants.INDEX_ONE);
                endDate = LocalDate.of(timeUnion, Constants.TWELVE, Constants.THIRTY_ONE);
            }
            if(MONTH.equals(timeCycle)){
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate.plusDays(startDate.lengthOfMonth()-1);
            }
            if(WEEK.equals(timeCycle)){
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate.plusDays(Constants.SIX_INT);
            }
            if(DAY.equals(timeCycle)){
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate;
            }
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endDateTime = LocalDateTime.of(endDate, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.TIME_DEAL_ERROR);
        }
        return new Pair<>(startDateTime, endDateTime);
    }

    /**
     * 获取时间范围
     * @param timeCycle
     * @param timeUnion
     * @return
     */
    public static Pair<LocalDate, LocalDate> getStartAndEndDate(TimeCycleEnum timeCycle, Integer timeUnion){
        if(Objects.isNull(timeCycle) || Objects.isNull(timeUnion)){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.now(), endDate = LocalDate.now();
        try {
            if(YEAR.equals(timeCycle)){
                startDate = LocalDate.of(timeUnion, Constants.INDEX_ONE, Constants.INDEX_ONE);
                endDate = LocalDate.of(timeUnion, Constants.TWELVE, Constants.THIRTY_ONE);
            }
            if(MONTH.equals(timeCycle)){
                timeUnion = timeUnion * Constants.ONE_HUNDRED + Constants.INDEX_ONE;
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate.plusDays(startDate.lengthOfMonth()-1);
            }
            if(WEEK.equals(timeCycle)){
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate.plusDays(Constants.SIX_INT);
            }
            if(DAY.equals(timeCycle)){
                startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
                endDate = startDate;
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.TIME_DEAL_ERROR);
        }
        return new Pair<>(startDate, endDate);
    }

    /**
     * 是否是当前日期
     * @param timeCycle
     * @param timeUnion
     * @return
     */
    public static boolean isCurrentDay(TimeCycleEnum timeCycle, Integer timeUnion){
        try {
            if(TimeCycleEnum.DAY.equals(timeCycle)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                return LocalDate.parse(String.valueOf(timeUnion), formatter).equals(LocalDate.now());
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.TIME_DEAL_ERROR);
        }
        return false;
    }

    public static Integer getCurrentDay(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.valueOf(LocalDate.now().format(formatter));
    }

    public static List<Integer> getTimeCycleList(TimeCycleEnum timeCycle, Integer timeUnion){
        List<Integer> resultList = new ArrayList<>();
        if(TimeCycleEnum.DAY.equals(timeCycle)){
            //获取一周七天的数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
            LocalDate monday = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            for (LocalDate indexDate = monday; indexDate.compareTo(sunday) <= Constants.INDEX_ZERO;){
                Integer time = Integer.valueOf(indexDate.format(formatter));
                resultList.add(time);
                indexDate = indexDate.plusDays(Constants.INDEX_ONE);
            }
        }
        if(TimeCycleEnum.WEEK.equals(timeCycle)){
            //一个月的第几周
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
            LocalDate firstDayOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDayOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
            LocalDate firstMonday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastMonday = lastDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            for (LocalDate indexDate = firstMonday; indexDate.compareTo(lastMonday) <= Constants.INDEX_ZERO;){
                Integer time = Integer.valueOf(indexDate.format(formatter));
                resultList.add(time);
                indexDate = indexDate.plusDays(Constants.SEVEN);
            }
        }
        if(TimeCycleEnum.MONTH.equals(timeCycle)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
            int year = startDate.getYear();
            LocalDate firstDay = LocalDate.of(year, Constants.INDEX_ONE, Constants.INDEX_ONE);
            LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfYear());
            for (LocalDate indexDate = firstDay; indexDate.isBefore(lastDay);){
                Integer time = Integer.valueOf(indexDate.format(formatter));
                resultList.add(time);
                indexDate = indexDate.plusMonths(Constants.INDEX_ONE);
            }
        }
        return resultList;
    }


    public static Integer getMaxTimeCycle(TimeCycleEnum timeCycle, Integer timeUnion){
        List<Integer> resultList = new ArrayList<>();
        if(TimeCycleEnum.DAY.equals(timeCycle)){
            return timeUnion;
        }
        if(TimeCycleEnum.WEEK.equals(timeCycle)){
            //一个月的第几周
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
            LocalDate endDate = startDate.plusDays(Constants.SIX);
            return Integer.valueOf(endDate.format(formatter));
        }
        if(TimeCycleEnum.MONTH.equals(timeCycle)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
            int monthDays = startDate.lengthOfMonth();
            LocalDate localDate = startDate.plusDays(monthDays - Constants.INDEX_ONE);
            return Integer.valueOf(localDate.format(formatter));
        }
        return timeUnion;
    }

    public static String getDayMinTime(Integer timeUnion){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
        return LocalDateTime.of(startDate, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getDayMaxTime(Integer timeUnion, TimeCycleEnum timeCycle){
        timeUnion = getMaxTimeCycle(timeCycle, timeUnion);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), formatter);
        return LocalDateTime.of(startDate, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public static List<String> getTimeCycleStrList(TimeCycleEnum timeCycle, Integer timeUnion){
        List<String> resultList = new ArrayList<>();
        if(TimeCycleEnum.DAY.equals(timeCycle)){
            //获取一周七天的数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate monday = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            for (LocalDate indexDate = monday; indexDate.compareTo(sunday) <= Constants.INDEX_ZERO;){
                resultList.add(indexDate.format(formatter));
                indexDate = indexDate.plusDays(Constants.INDEX_ONE);
            }
        }
        if(TimeCycleEnum.WEEK.equals(timeCycle)){
            //一个月的第几周
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate firstDayOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDayOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
            LocalDate firstMonday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastMonday = lastDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            for (LocalDate indexDate = firstMonday; indexDate.compareTo(lastMonday) <= Constants.INDEX_ZERO;){
                resultList.add(indexDate.format(formatter));
                indexDate = indexDate.plusDays(Constants.SEVEN);
            }
        }
        if(TimeCycleEnum.MONTH.equals(timeCycle)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(String.valueOf(timeUnion), DateTimeFormatter.ofPattern("yyyyMMdd"));
            int year = startDate.getYear();
            LocalDate firstDay = LocalDate.of(year, Constants.INDEX_ONE, Constants.INDEX_ONE);
            LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfYear());
            for (LocalDate indexDate = firstDay; indexDate.isBefore(lastDay);){
                resultList.add(indexDate.format(formatter));
                indexDate = indexDate.plusMonths(Constants.INDEX_ONE);
            }
        }
        return resultList;
    }
    public static void main(String[] args) {
        List<String> timeCycleList = getTimeCycleStrList(TimeCycleEnum.DAY, 20220919);
        for (int i = 0; i < timeCycleList.size(); i++) {
            System.out.println(timeCycleList.get(i));
        }

    }
}
