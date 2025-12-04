/*
 *
 *                  Copyright 2017 Crab2Died
 *                     All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Browse for more information ：
 * 1) https://gitee.com/Crab2Died/Excel4J
 * 2) https://github.com/Crab2died/Excel4J
 *
 */
package com.coolcollege.intelligent.common.util;


import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.date.PeriodEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.*;

/**
 * <p>时间处理工具类</p>
 * author : Crab2Died
 * date : 2017/5/23  10:35
 */
public class DateUtils {

    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DAY_2 = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String TIME_FORMAT_SEC = "HH:mm:ss";
    public static final String TIME_FORMAT_SEC2 = "HH:mm";
    public static final String DATE_FORMAT_SEC = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SEC_2 = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT_SEC_3 = "yyyy.MM.dd HH:mm:ss";
    public static final String DATE_FORMAT_SEC_4 = "yyyy.MM.dd HH:mm";
    public static final String DATE_FORMAT_SEC_5 = "yyyy.MM.dd HH:mm";
    public static final String DATE_FORMAT_SEC_6 = "yyyy.MM.dd";

    public static final String DATE_FORMAT_SEC_7 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_SEC_8 = "yyyyMMddHHmm";

    public static final String DATE_FORMAT_MSEC = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_MSEC_T = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_MSEC_T_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_DAY_SIMPLE = "y/M/d";
    public static final String DATE_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_MINUTE_2 = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_MINUTE_3 = "yyyy/MM/dd/HH/mm";

    public static final String DATE_FORMAT_HOUR_1 = "yyyy-MM-dd HH";


    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    //分，秒
    public static final int SEC_MAX = 1000*60;

    /**
     * 秒转时分
     * 28800 ->  08:00
     * @param time
     * @return
     */
    public static String secToTime(Long time) {
        StringBuilder stringBuilder = new StringBuilder();
        Long hour = time / 3600000;
        Long minute = time / 60000 % 60;
        if(hour<1) {
            stringBuilder.append(minute);
        }else {
            if (hour < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hour);
            stringBuilder.append("小时");
            if (minute < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(minute);
        }
        stringBuilder.append("分钟");
        return stringBuilder.toString();
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式
     */
    public static String convertTimeToString(Long time, String format){
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(format);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }
    /**
     * 将字符串转日期成Long类型的时间戳
     */
    public static Long convertStringToLong(String time) {
        Assert.notNull(time, "time is null");
        String patter = getPatter(time);
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(patter);
        LocalDateTime parse = LocalDateTime.parse(time, ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将字符串转日期成Long类型的时间戳
     */
    public static Long convertStringToLong(String time, String pattern) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime parse = LocalDateTime.parse(time, ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将字符串转日期成Long类型的时间戳
     */
    public static Long convertTimeToLong(LocalDateTime time) {
        Assert.notNull(time, "time is null");
        return LocalDateTime.from(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将字符串转日期成Long类型的时间戳
     */
    public static String convertDateToString(LocalDate date, boolean isStart) {
        Assert.notNull(date, "date is null");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_SEC);
        LocalTime localTime = LocalTime.MIN;
        if (!isStart) {
            localTime = LocalTime.MAX;
        }
        LocalDateTime time = LocalDateTime.of(date, localTime);
        return time.format(formatter);
    }

    public static String convertDateTimeToString(LocalDateTime time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_SEC);
        return time.format(formatter);
    }

    public static Date datePlusSeconds(Date date, Long amountToAdd) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return LocalDateTimeToUdate(localDateTime.plusSeconds(amountToAdd));
    }
    public static Date LocalDateTimeToUdate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
       return Date.from(instant);
    }
    public static Date localDate2Date(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 将字符串转日期
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDate(String time) {
        Assert.notNull(time, "time is null");
        String patter = getPatter(time);

        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(patter);
        return  LocalDateTime.parse(time, ftf);
    }

    /**
     * 将字符串转日期
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDate(String time, String pattern) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(time, ftf);
    }

    /**
     * 将date转成对应的int格式
     * @param date
     * @return
     */
    public static Long parseDateToLong(LocalDate date, boolean isStart) {
        Assert.notNull(date, "date is null");
        LocalTime localTime = LocalTime.MIN;
        if (!isStart) {
            localTime = LocalTime.MAX;
        }
        LocalDateTime time = LocalDateTime.of(date, localTime);
        return LocalDateTime.from(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    public static List<LocalDate> handleDate(LocalDate now, LocalDate startDate, LocalDate endDate, String period) {
        if (StrUtil.isNotBlank(period)) {
            if (period.equals(PeriodEnum.MONTH.getCode())) {
                startDate = now.plusDays(-30);
            }
            if (period.equals(PeriodEnum.WEEK.getCode())) {
                startDate = now.plusDays(-7);
            }
            if (period.equals(PeriodEnum.THREE.getCode())) {
                startDate = now.plusDays(-3);
            }
            if (period.equals(PeriodEnum.TODAY.getCode())) {
                startDate = now;
                endDate = now;
            }
        }
        return Arrays.asList(startDate, endDate);
    }

    public static Map<String, Object> getTimeMap(String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.plusDays(-1);
        LocalDate endDate = now.plusDays(-1);
        List<LocalDate> localDates = handleDate(now, startDate, endDate, period);
        Long startTime = LocalDateTime.from(LocalDateTime.of(localDates.get(0), LocalTime.MIN)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endTime = LocalDateTime.from(LocalDateTime.of(localDates.get(1), LocalTime.MAX)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }


    /**
     * 获取字符串匹配格式
     * @param time
     * @return
     */
    public static String getPatter(String time) {
        String patter = "";
        if (time.contains("-")) {
            patter = DATE_FORMAT_SEC;
        }
        if (time.contains("/")) {
            patter = DATE_FORMAT_SEC_2;
        }
        if (time.contains(".")) {
            patter = DATE_FORMAT_SEC_3;
        }
        return patter;
    }

    public static String formatTime(Long millisecond, TimeUnit unit) {
        DecimalFormat df = new DecimalFormat("0.00");
        int denominator = 1000;
        if (unit.equals(TimeUnit.SECONDS)) {
            return df.format((double) millisecond/ denominator);
        } else if (unit.equals(TimeUnit.MINUTES)) {
            return df.format((double) millisecond/ (denominator * 60));
        } else if (unit.equals(TimeUnit.HOURS)) {
            return df.format((double) millisecond/ (denominator * 3600));
        } else {
            return "";
        }
    }

    /**
     * 传入两个Long类型的时间戳 毫秒级
     * 判断是否是同一天
     * @param ms1
     * @param ms2
     * @return
     */
    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }


    public static long getDateTime(String time) {
        long times = 0;
        String[] split = time.split(":");
        times += Integer.parseInt(split[0]) * 3600 * 1000;
        times += Integer.parseInt(split[1]) * 60 * 1000;
        return times;
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    public static LocalDateTime getStartDayOfWeek(LocalDate date) {
        TemporalField fieldIso = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        LocalDate localDate = LocalDate.from(date);
        localDate = localDate.with(fieldIso, 1);
        return localDate.atStartOfDay();
    }

    public static LocalDateTime getEndDayOfWeek(LocalDate date) {
        TemporalField fieldIso = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        LocalDate localDate = LocalDate.from(date);
        localDate = localDate.with(fieldIso, 7);
        return localDate.atStartOfDay();
    }
    public static void checkDayInterval(Long beginDate,Long endDate,Long intervalDay){
        if(beginDate==null){
            throw new ServiceException(ACH_DATE_BEGIN_NOT_NULL);
        }
        if(endDate==null){
            throw new ServiceException(ACH_DATE_END_NOT_NULL);
        }
        LocalDateTime beginLocalDateTime =asLocalDateTime(beginDate);
        LocalDateTime endLocalDateTime =asLocalDateTime(endDate);
        long l = Duration.between(beginLocalDateTime, endLocalDateTime).toDays();
        if(l>=intervalDay){
            throw new ServiceException(ACH_DATE_MORE_THAN_DAY,intervalDay);
        }
    }
    private static void checkMonthInterval(Long beginDate,Long endDate,Long intervalMonth){

    }

    /**
     * Long 转LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime asLocalDateTime(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static String formatBetween(Date beginTime, Date endTime){
        if(endTime == null || beginTime == null){
            return null;
        }
        long tmp = endTime.getTime() - beginTime.getTime();
        return formatBetween(tmp);
    }

    public static String formatBetweenForSeconds(Date beginTime, Date endTime){
        if(endTime == null || beginTime == null){
            return null;
        }
        long l = endTime.getTime() - beginTime.getTime();
        return String.valueOf(l/1000);
    }

    /**
     * 获得两个时间相隔天数
     * @param beginTime
     * @param endTime
     * @author: xugangkun
     * @return java.lang.Long
     * @date: 2022/4/6 15:46
     */
    public static Long dayBetween(Date beginTime, Date endTime){
        if(endTime == null || beginTime == null){
            return null;
        }
        long tmp = endTime.getTime() - beginTime.getTime();
        long day = tmp/(1000*60*60*24);
        return day;
    }

    public static String formatBetween(long tmp){
        try {
            long day = tmp/(1000*60*60*24);
            tmp -=  day*1000*60*60*24;
            long hour = tmp/(1000*60*60);
            tmp -= hour*1000*60*60;
            long minute = tmp / (1000*60);
            tmp -= minute*1000*60;
            long second = tmp / 1000;

            if(day>0){
                return day+"天"+hour+"时"+minute+"分";
            }

            if(hour>0){
                return hour+"时"+minute+"分"+second+"秒";
            }

            return minute + "分" + second + "秒";
        } catch (Exception e) {
            return "0000";
        }
    }


    public static String calculateSecondsBetween(Date startTime, Date endTime) {
        Long l = Optional.ofNullable(startTime)
                .map(st -> Optional.ofNullable(endTime)
                        .map(et -> Math.abs(Duration.between(st.toInstant(), et.toInstant()).getSeconds()))
                        .orElse(0L))
                .orElse(0L);
        return l.toString();
    }

    /**
     * 获得两个时间中的每一天
     * @param bigTime
     * @param endTime
     * @author: xugangkun
     * @return java.util.List<java.util.Date>
     * @date: 2022/3/3 11:28
     */
    public static List<Date> getBetweenDate(Date bigTime, Date endTime) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(bigTime);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(bigTime);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endTime);
        // 测试此日期是否在指定日期之后
        while (endTime.after(calBegin.getTime()))  {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }

    public static List<Date> getBetweenMouthDate(Date bigTime, Date endTime) {
        List<Date> lDate = new ArrayList<>();
        lDate.add(bigTime);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(bigTime);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endTime);
        // 测试此日期是否在指定日期之后
        while (endTime.after(calBegin.getTime()))  {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    /**
     * 时间转标准日期
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        if(date == null){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_DAY);
        // new Date()为获取当前系统时间
        String time = df.format(date);
        return time;
    }

    public static Date transferString2Date(String s) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(DATE_FORMAT_MINUTE).parse(s);
        } catch (Exception e) {

        }
        return date;
    }

    public static Date getSpecialTime(Date date,Double limitHour){
        double time = limitHour;
        Calendar dar=Calendar.getInstance();
        dar.setTime(date);
        dar.add(java.util.Calendar.SECOND, (int) time);
        return dar.getTime();
    }

    /**
     * 获取当前时间的下一天
     * @param date
     * @return
     */
    public static Date getNextDayTime(Date date,Integer time){
        Calendar dar=Calendar.getInstance();
        dar.setTime(date);
        dar.add(Calendar.DATE,time);
        return dar.getTime();
    }


    /**
     * 下个月第一天
     * @param date
     * @return
     */
    public static Date firstDayOfNextMonth(Date date) {
        //获得入参的日期
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);

        //获取下个月第一天：
        cd.add(Calendar.MONTH, 1);
        //设置为1号,当前日期既为次月第一天
        cd.set(Calendar.DAY_OF_MONTH,1);

        return cd.getTime();
    }

    /**
     * 下周第一天
     * @param date
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        //获得入参的日期
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);

        // 获得入参日期是一周的第几天
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        // 获得入参日期相对于下周一的偏移量（在国外，星期一是一周的第二天，所以下周一是这周的第九天）
        // 若入参日期是周日，它的下周一偏移量是1
        int nextMondayOffset = dayOfWeek == 1 ? 1 : 9 - dayOfWeek;

        // 增加到入参日期的下周一
        cd.add(Calendar.DAY_OF_MONTH, nextMondayOffset);
        return cd.getTime();
    }

    /**
     * 将字符串转日期
     * @param timeUnion 20220920
     * @return
     */
    public static LocalDate convertTimeUnionToDate(Integer timeUnion) {
        int day = timeUnion % 100;
        int yearMonth = (timeUnion - day) / 100;
        int month = yearMonth % 100;
        int year = (yearMonth - month) / 100;
        return LocalDate.of(year, month, day);
    }


    public static List<String> getDayOfWeekWithinDateInterval(String beginTimeDate, String endTimeDate, String storeWorkCycleEnum) {
        if (StringUtils.isBlank(beginTimeDate) || StringUtils.isBlank(endTimeDate)) {
            return new ArrayList<>();
        }
        LocalDate beginDate = LocalDate.parse(beginTimeDate);
        LocalDate endDate = LocalDate.parse(endTimeDate);
        List<String> resultList = new ArrayList<>();
        //月
        if (StoreWorkCycleEnum.MONTH.getCode().equals(storeWorkCycleEnum)) {
            //每月第一天
            beginDate = LocalDate.of(beginDate.getYear(), beginDate.getMonth(), 1);
            long distance = ChronoUnit.MONTHS.between(beginDate, endDate);
            Stream.iterate(beginDate, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> resultList.add(f.toString()));
        } else if (StoreWorkCycleEnum.WEEK.getCode().equals(storeWorkCycleEnum)) {
            //周
            //每周周一
            beginDate = beginDate.with(DayOfWeek.MONDAY);
            long distance = ChronoUnit.WEEKS.between(beginDate, endDate);
            Stream.iterate(beginDate, d -> d.plusWeeks(1)).limit(distance + 1).forEach(f -> resultList.add(f.toString()));
        } else {
            //日
            long distance = ChronoUnit.DAYS.between(beginDate, endDate);
            Stream.iterate(beginDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> resultList.add(f.toString()));
        }
        return resultList;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param date
     * @return 当前日期是星期几
     */
    public static Integer getWeekOfDate(Date date) {
        Integer[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 当月的几号
     * @param date
     * @return
     */
    public static Integer  getMonthOfDate(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.DAY_OF_MONTH) ;
    }

    /**
     * 当月的几号
     * @param date
     * @return
     */
    public static Integer  getMonthOfYear(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.MONTH)+1;
    }

    /**
     * 当月的几号
     * @param date
     * @return
     */
    public static Integer  getWeekOfYear(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.WEEK_OF_YEAR) ;
    }


    public static String dateConvertString(Date date) {
        if(date == null){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // new Date()为获取当前系统时间
        String time = df.format(date);
        return time;
    }

    /**
     * yyyyMMddHHmmss格式的字符串转换为yyyy-MM-dd HH:mm:ss
     * @param dateStr 时间字符串
     * @return yyyy-MM-dd HH:mm:ss格式字符串
     */
    public static String dateTimeFormat(String dateStr) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = originalFormat.parse(dateStr);
            return targetFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date convertUtcTime(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        LocalDate startDate = LocalDate.now(); // 获取当前日期
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        List<String> workdays = new ArrayList<>();
        List<String> weekends = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String formattedDate = date +"";

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekends.add(formattedDate);
            } else {
                workdays.add(formattedDate);
            }
        }

        for (String workday : workdays) {
            String format = "HSETNX SwStoreWorkExcludeDateKey:6d7c080bd55c4768807a0793fe4f3b8a:91 %s '%s'";
            System.out.println(String.format(format, workday, workday));
        }

        System.out.println("---------------------------");

        for (String weekend : weekends) {
            String format = "HSETNX SwStoreWorkExcludeDateKey:6d7c080bd55c4768807a0793fe4f3b8a:90 %s '%s'";
            System.out.println(String.format(format, weekend, weekend));
        }
    }

    public static String getDateThreeDaysAgo() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysAgo = today.minusDays(3);
        return threeDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private static String formatList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n    ");
        sb.append(String.join(",\n    ", list));
        sb.append("\n  ]");
        return sb.toString();
    }

    //获取24个小时时间点
    public static List<String> getHourTimeList() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hour = String.format("%02d", i);
            hours.add(hour);
        }
        return hours;
    }

}
