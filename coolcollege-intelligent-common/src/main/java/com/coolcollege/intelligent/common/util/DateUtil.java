package com.coolcollege.intelligent.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.Week;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 时间工具
 */
public class DateUtil {

    /**
     * 一天的秒数
     */
    public static final int DAY_SECONDS = 60 * 60 * 24;

    /**
     * 一天的毫秒数
     */
    public static final long DAY_MILLI_SECONDS = 1000L * DAY_SECONDS;

    /**
     * UTC TIME ZONE
     */
    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("GMT");

    /**
     * 校验日期格式是否正确
     *
     * @param dateStr
     * @param pattern
     * @return true-正确；false-不正确
     */
    public static boolean valid(String dateStr, String pattern) {
        try {
            org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr
     * @param pattern
     * @return Date
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格式化日期，日期格式默认：yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return DateFormatUtils.format(date, DatePattern.NORM_DATE_PATTERN);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if(Objects.isNull(date)){
            return null;
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 格式化日期
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期字符串转换成特定日期格式的字符串
     * <p>
     * 例如：20200505 转换成 2020-05-05 从 20200505121214 转换成 2020-050-51 21:21:43
     *
     * @param dateStr       日期字符串
     * @param sourcePattern 原始日期格式
     * @param targetPattern 目标日期格式
     * @return
     */
    public static String convert(String dateStr, String sourcePattern, String targetPattern) {
        return format(parse(dateStr, sourcePattern), targetPattern);
    }

    /**
     * 将时间转换成Long型
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return Long型日期
     */
    public static Long convert(Date date, String pattern) {
        return Long.valueOf(format(date, pattern));
    }

    /**
     * LocaleTime 转换为Date
     *
     * @param localTime
     * @return
     */
    public static Date convert(LocalDate nowDate, LocalTime localTime) {
        Date date = null;
        if (localTime != null) {
            LocalDateTime localDateTime = LocalDateTime.of(nowDate, localTime);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            date = Date.from(instant);
        }
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime nowTime() {
        return LocalDateTime.now();
    }


    /**
     * 获取当前时间毫秒数
     *
     * @return 当前时间毫秒数
     */
    public static Long currentMilliseconds() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间秒数
     *
     * @return 当前时间秒数
     */
    public static Integer currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * LocalDate To Date
     *
     * @param localDate
     * @return
     */
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime To Date
     *
     * @param localDateTime
     * @return
     */
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date To LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * dateStr To LocalDate
     *
     * @param dateStr 日期字符串，格式为yyyyMMdd
     * @return
     */
    public static LocalDate asLocalDate(String dateStr) {
        return asLocalDate(parse(dateStr, DatePattern.PURE_DATE_PATTERN));
    }

    /**
     * Date To LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * timestamp To LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime asLocalDateTime(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static Long getTimestamp() {
        return getTimestamp(LocalDateTime.now());
    }


    /**
     * 获取指定日期的时间戳
     *
     * @param date 日期
     * @return
     */
    public static Long getTimestamp(Date date) {
        return getTimestamp(asLocalDateTime(date));
    }

    /**
     * 获取指定时间的时间戳
     *
     * @param localDateTime 时间
     * @return
     */
    public static Long getTimestamp(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 获取当前时间截取的时间毫秒数，精确度到DATE
     */
    public static Long getTruncatedTimestamp() {
        return getTruncatedTimestamp(new Date(), Calendar.DATE);
    }

    /**
     * 获取指定日期截取的时间毫秒数
     *
     * @param date 指定日期
     * @return field 精确的粒度，例如：Calendar.DATE
     */
    public static Long getTruncatedTimestamp(Date date, int field) {
        return org.apache.commons.lang3.time.DateUtils.truncate(date, field).getTime();
    }

    /**
     * 获取指定日期的开始时间戳
     *
     * @return
     */
    public static Long getBeginTimestampOfDay(Date date) {
        String dateStr = cn.hutool.core.date.DateUtil.format(date, DatePattern.PURE_DATE_PATTERN);
        if (dateStr == null) {
            return null;
        }
        Date targetDate = cn.hutool.core.date.DateUtil.parse(dateStr, DatePattern.PURE_DATE_PATTERN);
        return targetDate.getTime();
    }

    /**
     * 获取获取指定日期下一天的开始时间戳
     *
     * @return
     */
    public static Long getBeginTimestampOfNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return getBeginTimestampOfDay(calendar.getTime());
    }

    /**
     * 获取指定日期的开始时间
     *
     * @param date
     * @return
     */
    public static Date getBeginOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的结束时间
     *
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date setMillisecondToZero(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的结束时间精确到3位毫秒
     *
     * @param date
     * @return
     */
    public static Date getEndMillSecondOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取指定日期所在月份的第一天
     *
     * @param date 日期
     * @return 该日期所在月份第一天
     */
    public static Date getFirstOfDayMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int first = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, first);
        // 设置时间为第1秒
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定日期所在月份的最后一天
     *
     * @param date 日期
     * @return 该日期所在月份的最后一天
     */
    public static Date getLastOfDayMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        // 设置时间为最后1秒
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取指定日期所在月份的最后一天精确到3位毫秒
     *
     * @param date 日期
     * @return 该日期所在月份的最后一天
     */
    public static Date getLastMillSecondOfMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        // 设置时间为最后1秒
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取指定时间所在月份的第一天0点
     *
     * @param timestamp 时间戳
     * @return
     */
    public static LocalDateTime getFirstOfDayMonth(Long timestamp) {
        //默认本月
        if (timestamp == null) {
            return getFirstOfDayMonth(currentMilliseconds());
        }
        return asLocalDateTime(getTruncatedTimestamp(new Date(timestamp), Calendar.MONTH));
    }

    /**
     * 获取指定时间下个月份的第一天0点
     *
     * @param timestamp 时间戳
     * @return
     */
    public static LocalDateTime getFirstOfDayNextMonth(Long timestamp) {
        //默认为当前时间
        if (timestamp == null) {
            return getFirstOfDayNextMonth(currentMilliseconds());
        }
        Date nextMonthDay = org.apache.commons.lang3.time.DateUtils.addMonths(new Date(timestamp), 1);
        return asLocalDateTime(getTruncatedTimestamp(nextMonthDay, Calendar.MONTH));
    }

    /**
     * 获取指定日期所在月份的第一天日期
     *
     * @param date 格式：201809
     * @return 格式：2018-09-01
     */
    public static String getFirstDayOfMonth(String date) {
        return asLocalDate(date).toString();
    }

    /**
     * 获取指定日期所在月份的下个月第一天的日期
     *
     * @param date 格式：201809
     * @return 格式：2018-10-01
     */
    public static String getFirstDayOfNextMonth(String date) {
        return asLocalDate(date).with(TemporalAdjusters.lastDayOfMonth()).minusDays(-1).toString();
    }

    /**
     * 获取昨日月份的第一天到昨天的日期集合
     * <p>
     * 例如：
     * 1、今天是20200210，集合返回20200201-20200209
     * 2、今天是20200301，集合返回20200201-20200229
     */
    public static Set<String> getDateSetByLastDay() {
        Set<String> set = new LinkedHashSet<>();
        int day = LocalDate.now().getDayOfMonth();
        int nowDay = 0;
        if (day == 1) {
            nowDay = LocalDate.now().plusDays(-1).lengthOfMonth();
            for (int i = nowDay; i >= 1; i--) {
                int d = Integer.valueOf("-" + i);
                set.add(LocalDate.now().plusDays(d).toString());
            }
        } else {
            nowDay = LocalDate.now().getDayOfMonth();
            for (int i = nowDay - 1; i >= 1; i--) {
                int d = Integer.valueOf("-" + i);
                set.add(LocalDate.now().plusDays(d).toString());
            }
        }

        return set;
    }

    /**
     * 获取指定日期在当前周里的索引
     * 说明：1~7 对应 周一~周日
     *
     * @param date
     * @return
     */
    public static int getWeekDayIndex(Date date) {
        int i = DateTime.of(date).dayOfWeek();
        i--;
        return i == 0 ? 7 : i;
    }

    /**
     * 获取指定日期在当前月第几天
     *
     * @param date
     * @return
     */
    public static int geDayOfMonthIndex(Date date) {
        int i = DateTime.of(date).dayOfMonth();
        return i;
    }

    /**
     * 获取指定日期所在月天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 把英文类型的星期转为中文
     *
     * @param localDate
     * @return
     */
    public static String getChineseWeek(LocalDate localDate) {
        switch (localDate.getDayOfWeek()) {
            case MONDAY:
                return Week.MONDAY.toChinese();
            case TUESDAY:
                return Week.TUESDAY.toChinese();
            case WEDNESDAY:
                return Week.WEDNESDAY.toChinese();
            case THURSDAY:
                return Week.THURSDAY.toChinese();
            case FRIDAY:
                return Week.FRIDAY.toChinese();
            case SATURDAY:
                return Week.SATURDAY.toChinese();
            case SUNDAY:
                return Week.SUNDAY.toChinese();
            default:
                return StringUtils.EMPTY;
        }
    }


    /**
     * 根据生日计算当前周岁数
     *
     * @param birthday 生日的年月日 例如 1992-08-06
     * @param pattern  日期格式
     * @return 参数为空时候, 默认0岁, 否则按照实际计算周岁
     */
    public static Integer getCurrentAge(String birthday, String pattern) {
        if (birthday == null) {
            return null;
        }
        try {
            Date date = parse(birthday, pattern);
            return getCurrentAge(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据生日计算当前周岁数
     *
     * @param birthday 生日的系统毫秒数
     * @return 参数为空时候, 默认0岁, 否则按照实际计算周岁
     */
    public static Integer getCurrentAge(Long birthday) {
        if (birthday == null) {
            return null;
        }
        return getCurrentAge(asLocalDateTime(birthday));
    }

    /**
     * 根据生日计算当前周岁数
     *
     * @param birthday 生日的LocalD阿特Ti么
     * @return 参数为空时候, 默认0岁, 否则按照实际计算周岁
     */
    public static Integer getCurrentAge(LocalDateTime birthday) {
        return getCurrentAge(asDate(birthday), null);
    }

    /**
     * 计算生日与指定日期的年龄差，周岁数
     *
     * @param birthday  生日日期
     * @param referDate 指定日期
     * @return
     */
    public static Integer getCurrentAge(LocalDateTime birthday, LocalDateTime referDate) {
        return getCurrentAge(asDate(birthday), asDate(referDate));
    }

    /**
     * 根据生日计算当前周岁数
     *
     * @param birthday 生日的系统毫秒数
     * @return 参数为空时候, 默认0岁, 否则按照实际计算周岁
     */
    public static Integer getCurrentAge(Date birthday) {
        return getCurrentAge(birthday, null);
    }

    /**
     * 计算生日与指定日期的年龄差，周岁数
     *
     * @param birthday  生日日期
     * @param referDate 指定日期
     * @return
     */
    public static Integer getCurrentAge(Date birthday, Date referDate) {
        if (birthday == null) {
            return null;
        }
        // 参照时间
        Calendar curr = Calendar.getInstance();
        if (referDate != null) {
            curr.setTime(referDate);
        }
        // 生日
        Calendar born = Calendar.getInstance();
        born.setTime(birthday);
        // 年龄 = 当前年 - 出生年
        int age = curr.get(Calendar.YEAR) - born.get(Calendar.YEAR);
        if (age <= 0) {
            return 0;
        }
        // 如果当前月份小于出生月份: age-1
        // 如果当前月份等于出生月份, 且当前日小于出生日: age-1
        int currMonth = curr.get(Calendar.MONTH);
        int currDay = curr.get(Calendar.DAY_OF_MONTH);
        int bornMonth = born.get(Calendar.MONTH);
        int bornDay = born.get(Calendar.DAY_OF_MONTH);
        if ((currMonth < bornMonth) || (currMonth == bornMonth && currDay < bornDay)) {
            age--;
        }
        return age < 0 ? 0 : age;
    }

    /**
     * 获取指定时间到当前时间的天数
     *
     * @param timestamp 起始时间戳
     * @return
     */
    public static Integer getBetweenDays(Long timestamp) {
        return getBetweenDays(timestamp, getTruncatedTimestamp());
    }

    /**
     * 获取两个时间点之间的天数
     *
     * @param startTimestamp 起始时间戳
     * @param endTimestamp   结束时间戳
     * @return
     */
    public static Integer getBetweenDays(Long startTimestamp, Long endTimestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(startTimestamp));
        LocalDate startDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date(endTimestamp));
        LocalDate endDate = LocalDate.of(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DATE));

        return getBetweenDays(startDate, endDate);
    }

    /**
     * 获取两个时间点之间的天数
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return
     */
    public static Integer getBetweenDays(LocalDate startDate, LocalDate endDate) {
        Long count = startDate.toEpochDay() - endDate.toEpochDay();
        return count.intValue() + 1;
    }

    /**
     * 是否为上午
     *
     * @param date 日期
     * @return 是否为上午
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static boolean isAM(Date date) {
        return cn.hutool.core.date.DateUtil.isAM(date);
    }

    /**
     * 是否为下午
     *
     * @param date 日期
     * @return 是否为下午
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static boolean isPM(Date date) {
        return cn.hutool.core.date.DateUtil.isPM(date);
    }

    /**
     * 获取当前日期以后N天日期
     * <p>
     * 如：2018-08-07，3天后日期为 2018-08-10
     */
    public static Date plusDays(int days) {
        return plusDays(asDate(LocalDate.now()), days);
    }

    /**
     * 获取指定日期以后N天日期
     * <p>
     * 如：2018-08-07，3天后日期为 2018-08-10
     */
    public static Date plusDays(Date date, int days) {
        LocalDate localDate = date
                .toInstant()
                .atZone(ZoneId.systemDefault()).plusDays(days)
                .toLocalDate();
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 精确到秒的天数偏移days
     *
     * @param datetime
     * @param days
     * @return
     */
    public static LocalDateTime plusDays(LocalDateTime datetime, int days) {
        return datetime.plusDays(days);
    }

    /**
     * 获取今日时间
     * @param i
     * @return
     */
    public static Long getTodayTime(int i) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, i);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    public static String getDate(Long now) {
        Date date = new Date(now);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String s = df.format(date);
        Integer a = Integer.valueOf(s);
        return a.toString();
    }

    public static String getWeek(Long now) {
        Date today = new Date(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK)-1;
        return String.valueOf(weekday);
    }

    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

    public static Integer getSysYear() {
        Calendar date = Calendar.getInstance();
        return date.get(Calendar.YEAR);
    }


    /**
     * long类型转换成日期
     *
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static Date longToDate(long lo) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //long转Date
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sd.format(new Date(lo)));
        return date;
    }

    /**
     * 当前时间+一天
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    public static String format(Long time, String pattern) {
        if(Objects.isNull(time)){
            return null;
        }
        Date date = new Date(time);
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * LocalDateTime转换成毫秒时间戳
     */
    public static Long toMillis(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Date convertTimestampToDate(Long timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        return new Date(timestamp);
    }
}
