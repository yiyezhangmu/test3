package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: SubmitWeeklyNewspaperDTO
 * @Description: 提交周报
 * @date 2023-04-06 10:52
 */
@Data
public class WeeklyNewspaperDetailVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("所在周的周一 yyyy-MM-dd")
    private String beginDate;

    @ApiModelProperty("所在周的周日 yyyy-MM-dd")
    private String endDate;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("总结")
    private String summary;

    @ApiModelProperty("下一周计划")
    private String nextWeekPlan;

    @ApiModelProperty("竞品收集")
    private String competeProductCollect;

    @ApiModelProperty("提交时间")
    private Date createTime;

    @ApiModelProperty("附件url")
    private String fileUrl;

    @ApiModelProperty("已读人数")
    private Integer readNum;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("三方deptId")
    private String dingDeptId;

    @ApiModelProperty("媒体url")
    private String videoUrl;

    public static WeeklyNewspaperDetailVO convert(QyyWeeklyNewspaperDO weeklyNewspaper, String storeName, BigDecimal salesRate){
        if(Objects.isNull(weeklyNewspaper)){
            return null;
        }
        WeeklyNewspaperDetailVO result = new WeeklyNewspaperDetailVO();
        result.setId(weeklyNewspaper.getId());
        result.setStoreId(weeklyNewspaper.getStoreId());
        LocalDate parse = LocalDate.parse(weeklyNewspaper.getMondayOfWeek());
        result.setStoreName(storeName);
        result.setUsername(weeklyNewspaper.getUsername());
        result.setBeginDate(weeklyNewspaper.getMondayOfWeek());
        result.setEndDate(dateConvert(weeklyNewspaper.getMondayOfWeek()));
        result.setSalesRate(salesRate);
        result.setSummary(weeklyNewspaper.getSummary());
        result.setNextWeekPlan(weeklyNewspaper.getNextWeekPlan());
        result.setCompeteProductCollect(weeklyNewspaper.getCompeteProductCollect());
        result.setCreateTime(weeklyNewspaper.getCreateTime());
        result.setReadNum(weeklyNewspaper.getReadNum());
        result.setDingDeptId(weeklyNewspaper.getDingDeptId());
        result.setFileUrl(weeklyNewspaper.getFileUrl());
        result.setVideoUrl(weeklyNewspaper.getVideoUrl());
        return result;
    }

    /**
     * 根据开始日期计算结束日期
     * @param endTime
     * @return
     */
    public static String dateConvert(String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化工具类实例化创建
        Date date = new Date();
        try {
            date = sdf.parse(endTime); //格式化来源初值
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance(); //日历时间工具类实例化创建，取得当前时间初值
        calendar.setTime(date);  //覆盖掉当前时间
        calendar.add(Calendar.DATE, 6); //+6
        return sdf.format(calendar.getTime());
    }

}
