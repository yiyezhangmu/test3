package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: SubmitWeeklyNewspaperDTO
 * @Description: 提交周报
 * @date 2023-04-06 10:52
 */
@Data
public class WeeklyNewspaperPageVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("用户id")
    private String userId;

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

    @ApiModelProperty("已读人数")
    private String readNum;

    @ApiModelProperty("三方部门id")
    private String dingDeptId;

    private String fileUrl;

    private String videoUrl;

    private String thirdDingDeptId;

    public static List<WeeklyNewspaperPageVO> convert(List<QyyWeeklyNewspaperDO> list, Map<String, String> storeNameMap, Map<String, BigDecimal> salesRateMap){
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        List<WeeklyNewspaperPageVO> resultList = new ArrayList<>();
        for (QyyWeeklyNewspaperDO weeklyNewspaper : list) {
            WeeklyNewspaperPageVO result = new WeeklyNewspaperPageVO();
            result.setId(weeklyNewspaper.getId());
            result.setStoreId(weeklyNewspaper.getStoreId());
            LocalDate parse = LocalDate.parse(weeklyNewspaper.getMondayOfWeek());
            String endDate = parse.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toString();
            result.setStoreName(storeNameMap.get(weeklyNewspaper.getStoreId()));
            result.setUserId(weeklyNewspaper.getUserId());
            result.setUsername(weeklyNewspaper.getUsername());
            result.setBeginDate(weeklyNewspaper.getMondayOfWeek());
            result.setEndDate(endDate);
            result.setSalesRate(salesRateMap.get(weeklyNewspaper.getRegionId()+ Constants.MOSAICS + weeklyNewspaper.getMondayOfWeek()));
            result.setSummary(weeklyNewspaper.getSummary());
            result.setNextWeekPlan(weeklyNewspaper.getNextWeekPlan());
            result.setCompeteProductCollect(weeklyNewspaper.getCompeteProductCollect());
            result.setFileUrl(weeklyNewspaper.getFileUrl());
            result.setVideoUrl(weeklyNewspaper.getVideoUrl());
            resultList.add(result);
        }
        return resultList;
    }

}
