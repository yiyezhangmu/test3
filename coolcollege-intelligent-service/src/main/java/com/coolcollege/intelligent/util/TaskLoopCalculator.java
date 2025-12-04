package com.coolcollege.intelligent.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * describe: 任务轮次计算器
 *
 * @author wangff
 * @date 2025/2/7
 */
public class TaskLoopCalculator {

    /**
     * 计算已经经过多少轮
     * @param beginTime 开始时间，yyyy-MM-dd
     * @param endTime 结束时间
     * @param calendarTime 执行时间，HH:mm
     * @param taskCycle 任务周期
     * @param runDate 循环周期
     * @return 已经经过的轮次
     */
    public static long calculateExecutions(String beginTime, String endTime, String calendarTime, String taskCycle, String runDate) {
        LocalDate startDate = parseDate(beginTime);
        LocalDate endDate = parseDate(endTime);
        LocalTime execTime = parseTime(calendarTime);
        LocalDateTime current = LocalDateTime.now();

        if (current.toLocalDate().isBefore(startDate) || endDate.isBefore(startDate)) {
            return 0;
        }

        switch (taskCycle) {
            case "DAY":
                return calculateDaily(startDate, endDate, execTime, current);
            case "WEEK":
                return calculateWeekly(startDate, endDate, execTime, parseNumbers(runDate), current);
            case "MONTH":
                return calculateMonthly(startDate, endDate, execTime, parseNumbers(runDate), current);
            case "QUARTER":
                return calculateQuarterly(startDate, endDate, execTime, parseDate(runDate), current);
            default:
                throw new IllegalArgumentException("Invalid task cycle");
        }
    }

    private static long calculateDaily(LocalDate start, LocalDate end, LocalTime execTime, LocalDateTime current) {
        if (current.toLocalDate().isAfter(end)) {
            return ChronoUnit.DAYS.between(start, end) + 1;
        } else {
            return ChronoUnit.DAYS.between(start, current) + (!current.toLocalTime().isBefore(execTime) ? 1 : 0);
        }
    }

    private static long calculateWeekly(LocalDate start, LocalDate end, LocalTime execTime,
                                       Set<Integer> weekDays, LocalDateTime current) {
        long count = 0;
        LocalDate cursor = start.isAfter(current.toLocalDate()) ? start : current.toLocalDate().isBefore(end)
                ? start : end;

        for (; !cursor.isAfter(end) && !cursor.isAfter(current.toLocalDate()); cursor = cursor.plusDays(1)) {
            if (weekDays.contains(cursor.getDayOfWeek().getValue()) && !cursor.atTime(execTime).isAfter(current)) {
                count++;
            }
        }
        return count;
    }

    private static long calculateMonthly(LocalDate start, LocalDate end, LocalTime execTime,
                                        Set<Integer> monthDays, LocalDateTime current) {
        long count = 0;
        YearMonth currentYm = YearMonth.from(start);
        YearMonth endYm = YearMonth.from(end);

        while (!currentYm.isAfter(endYm)) {
            for (int day : monthDays) {
                if (day > currentYm.lengthOfMonth()) continue;

                LocalDate date = currentYm.atDay(day);
                LocalDateTime execDateTime = date.atTime(execTime);

                if (date.isBefore(start) || date.isAfter(end) || execDateTime.isAfter(current)) continue;

                count++;
            }
            currentYm = currentYm.plusMonths(1);
        }
        return count;
    }

    private static long calculateQuarterly(LocalDate start, LocalDate end, LocalTime execTime,
                                          LocalDate quarterStart, LocalDateTime current) {
        long count = 0;
        LocalDate cursor = quarterStart.isBefore(start) ?
                start.plusMonths((3 - (start.getMonthValue() - quarterStart.getMonthValue()) % 3) % 3) :
                quarterStart;

        while (!cursor.isAfter(end)) {
            LocalDateTime execDateTime = cursor.atTime(execTime);
            if (!cursor.isBefore(start) && !execDateTime.isAfter(current)) {
                count++;
            }
            cursor = cursor.plusMonths(3);
        }
        return count;
    }

    private static LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }

    private static LocalTime parseTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static Set<Integer> parseNumbers(String input) {
        return Arrays.stream(input.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
