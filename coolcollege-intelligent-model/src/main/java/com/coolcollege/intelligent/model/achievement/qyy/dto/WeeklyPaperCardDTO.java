package com.coolcollege.intelligent.model.achievement.qyy.dto;

import com.coolcollege.intelligent.model.achievement.qyy.vo.SalesReportVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
public class WeeklyPaperCardDTO extends WeeklyNewspaperDetailVO {

    @ApiModelProperty("查看周报链接")
    private String pcLookWeekLyPaperUrl;

    @ApiModelProperty("查看周报链接")
    private String iosLookWeekLyPaperUrl;

    @ApiModelProperty("查看周报链接")
    private String androidLookWeekLyPaperUrl;

    @ApiModelProperty("本周目标")
    private BigDecimal weekTarget;

    @ApiModelProperty("客单价")
    private BigDecimal customerPrice;

    @ApiModelProperty("毛利率")
    private BigDecimal grossMargin;

    @ApiModelProperty("连带率")
    private BigDecimal cascadeRate;

    @ApiModelProperty("分公司排名")
    private Integer branchRanking;

    @ApiModelProperty("全国排名")
    private Integer worldRanking;

    @ApiModelProperty("区域名")
    private String regionName;

    @ApiModelProperty("周报开始范围")
    private String startTime;

    @ApiModelProperty("周报结束时间")
    private String endTime;

    /**
     * 周业绩
     */
    private BigDecimal weekAchieve;

    /**
     * 周连带率
     */
    private BigDecimal weekAssociatedRate;

    /**
     * 月目标
     */
    private BigDecimal  monthTarget;

    /**
     * 月达成率
     */
    private BigDecimal monthAchieveRate;

    /**
     * 月客单价
     */
    private BigDecimal nationalRank;

    /**
     * 分公司排名
     */
    private String compRank;

    public static WeeklyPaperCardDTO covert(WeeklyNewspaperDetailVO param) {
        WeeklyPaperCardDTO weeklyPaperCardDTO = new WeeklyPaperCardDTO();
        weeklyPaperCardDTO.setId(param.getId());
        weeklyPaperCardDTO.setStoreId(param.getStoreId());
        weeklyPaperCardDTO.setStoreName(param.getStoreName());
        weeklyPaperCardDTO.setUsername(param.getUsername());
        weeklyPaperCardDTO.setBeginDate(param.getBeginDate());
        weeklyPaperCardDTO.setEndDate(param.getEndDate());
        weeklyPaperCardDTO.setSalesRate(param.getSalesRate());
        weeklyPaperCardDTO.setSummary(param.getSummary());
        weeklyPaperCardDTO.setNextWeekPlan(param.getNextWeekPlan());
        weeklyPaperCardDTO.setCompeteProductCollect(param.getCompeteProductCollect());
        weeklyPaperCardDTO.setCreateTime(param.getCreateTime());
        return weeklyPaperCardDTO;
    }


    public static WeeklyPaperCardDTO covert(WeeklyNewspaperDetailVO param1, AchieveQyyRegionDataDO param2, RegionDO param3,String eid) {

        WeeklyPaperCardDTO covert = covert(param1);
        if (!"25ae082b3947417ca2c835d8156a8407".equals(eid)){
            covert.setWeekTarget(param2.getSalesAmt());
            covert.setCustomerPrice(param2.getCusPrice());
            covert.setGrossMargin(param2.getProfitRate());
            covert.setCascadeRate(param2.getJointRate());
            covert.setBranchRanking(param2.getTopComp());
            covert.setWorldRanking(param2.getTopTot());
        }
        covert.setRegionName(param3.getName());
        covert.setStartTime(param1.getBeginDate());
        covert.setEndTime(dateConvert(param1.getBeginDate()));
        return covert;
    }


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
