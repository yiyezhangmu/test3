package com.coolcollege.intelligent.common.util;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 日清检查表table标签工具类
 *
 * @author byd
 * @date 2022-10-13 16:47
 */
public class TableInfoLabelUtil {
    private final static String TIME_RANGE = "timeRange";

    public static String getLabel(String tableInfo, String workCycle) {
        if (StringUtils.isBlank(tableInfo)) {
            return null;
        }
        JSONObject timeRangeObj = JSONObject.parseObject(tableInfo);
        List<TimeRangeDTO> list = JSONObject.parseArray(timeRangeObj.getString(TIME_RANGE), TimeRangeDTO.class);
        TimeRangeDTO begin = list.get(0);
        TimeRangeDTO end = list.get(1);
        String beginTime = begin.getHour().getLabel() + ":" + begin.getMinutes().getLabel();
        String isNextDay = end.getDay().getValue() == 1 ? "" : end.getDay().getLabel() + " ";
        String timeLabel = null;
        if (StoreWorkCycleEnum.DAY.getCode().equalsIgnoreCase(workCycle)) {
            timeLabel = beginTime + "~" + isNextDay + end.getHour().getLabel() + ":" + end.getMinutes().getLabel();

        } else if (StoreWorkCycleEnum.WEEK.getCode().equalsIgnoreCase(workCycle)) {
            timeLabel = begin.getWeek().getLabel() + beginTime + "~" + end.getWeek().getLabel()
                    + isNextDay + end.getHour().getLabel() + ":" + end.getMinutes().getLabel();

        } else if (StoreWorkCycleEnum.MONTH.getCode().equalsIgnoreCase(workCycle)) {
            timeLabel = begin.getMonth().getLabel() + beginTime + "~" + end.getMonth().getLabel() + isNextDay +
                    end.getHour().getLabel() + ":" + end.getMinutes().getLabel();
        }
        return timeLabel;
    }

    @Data
    public static class TimeRangeDTO {
        private TimeLabelDTO month;
        private TimeLabelDTO week;
        private TimeLabelDTO day;
        private TimeLabelDTO hour;
        private TimeLabelDTO minutes;

    }

    @Data
    public static class TimeLabelDTO {
        private String label;
        private Integer value;
    }



    public static String getTimeRange(Date storeWorkDate, String workCycle) {
        LocalDateTime startDate = LocalDateTime.ofInstant(storeWorkDate.toInstant(),
                ZoneId.systemDefault());
        String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeRange = null;
        if (StoreWorkCycleEnum.DAY.getCode().equalsIgnoreCase(workCycle)) {
            timeRange = startDateStr;
        } else if (StoreWorkCycleEnum.WEEK.getCode().equalsIgnoreCase(workCycle)) {
            String endDateStr = startDate.plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            timeRange = startDateStr + "~" + endDateStr;
        } else if (StoreWorkCycleEnum.MONTH.getCode().equalsIgnoreCase(workCycle)) {
            String month = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            timeRange = month + "月";
        }
        return timeRange;
    }


    public static void main(String[] args) {
        System.out.println(TableInfoLabelUtil.getLabel("{\"timeRange\":[{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"当日\",\"value\":1},\"hour\":{\"label\":\"00\",\"value\":0},\"minutes\":{\"label\":\"00\",\"value\":0}},{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"次日\",\"value\":2},\"hour\":{\"label\":\"01\",\"value\":1},\"minutes\":{\"label\":\"01\",\"value\":1}}],\"timeLabel\":\"00:00~次日 01:01\"}", "DAY"));
        System.out.println(TableInfoLabelUtil.getLabel("{\"timeRange\":[{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"当日\",\"value\":1},\"hour\":{\"label\":\"01\",\"value\":1},\"minutes\":{\"label\":\"01\",\"value\":1}},{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"次日\",\"value\":2},\"hour\":{\"label\":\"02\",\"value\":2},\"minutes\":{\"label\":\"02\",\"value\":2}}]}", "WEEK"));
        System.out.println(TableInfoLabelUtil.getLabel("{\"timeRange\":[{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"当日\",\"value\":1},\"hour\":{\"label\":\"01\",\"value\":1},\"minutes\":{\"label\":\"01\",\"value\":1}},{\"month\":{\"label\":\"\",\"value\":-1},\"week\":{\"label\":\"\",\"value\":-1},\"day\":{\"label\":\"次日\",\"value\":2},\"hour\":{\"label\":\"02\",\"value\":2},\"minutes\":{\"label\":\"02\",\"value\":2}}]}", "MONTH"));

    }
}
