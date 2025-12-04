package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AchieveReportListRes {

    @ApiModelProperty("总销售额（业绩）")
    private BigDecimal grossSales;


    @ApiModelProperty("销售额达成率")
    private BigDecimal grossSalesRate;

    //
    @ApiModelProperty("单产")
    private BigDecimal output;

    //
    @ApiModelProperty("单产达成率")
    private BigDecimal outputRate;

    //
    @ApiModelProperty("销量")
    private BigDecimal salesVolume;
    //
    @ApiModelProperty("销量达成率")
    private BigDecimal salesVolumeRate;

    //
    @ApiModelProperty("客单价")
    private BigDecimal perCustomer;
    //
    @ApiModelProperty("客单价达成率")
    private BigDecimal perCustomerRate;

    private Date updateTime;

    @ApiModelProperty("子列表")
    private List<SubRegion> subRegionList;

    public AchieveReportListRes convert(QyyPerformanceReportDO qyyPerformanceReportDO,List<QyyPerformanceReportDO> qyyPerformanceReportList) {
        if (qyyPerformanceReportDO == null) {
            return null;
        }
        AchieveReportListRes achieveReportListRes = new AchieveReportListRes();
        achieveReportListRes.setGrossSales(qyyPerformanceReportDO.getGrossSales());
        achieveReportListRes.setGrossSalesRate(qyyPerformanceReportDO.getGrossSalesRate());
        achieveReportListRes.setOutput(qyyPerformanceReportDO.getOutput());
        achieveReportListRes.setOutputRate(qyyPerformanceReportDO.getOutputRate());
        achieveReportListRes.setSalesVolume(qyyPerformanceReportDO.getSalesVolume());
        achieveReportListRes.setSalesVolumeRate(qyyPerformanceReportDO.getSalesVolumeRate());
        achieveReportListRes.setPerCustomer(qyyPerformanceReportDO.getPerCustomer());
        achieveReportListRes.setPerCustomerRate(qyyPerformanceReportDO.getPerCustomerRate());
        achieveReportListRes.setUpdateTime(qyyPerformanceReportDO.getUpdateTime());
        List<SubRegion> subRegionList = new ArrayList<>();
        for (QyyPerformanceReportDO performanceReportDO : qyyPerformanceReportList) {
            SubRegion subRegion = new SubRegion();
            subRegion.setStoreName(performanceReportDO.getStoreName());
            subRegion.setGrossSales(performanceReportDO.getGrossSales());
            subRegion.setGrossSalesRate(performanceReportDO.getGrossSalesRate());
            subRegion.setOutput(performanceReportDO.getOutput());
            subRegion.setOutputRate(performanceReportDO.getOutputRate());
            subRegion.setSalesVolume(performanceReportDO.getSalesVolume());
            subRegion.setSalesVolumeRate(performanceReportDO.getSalesVolumeRate());
            subRegion.setPerCustomer(performanceReportDO.getPerCustomer());
            subRegion.setPerCustomerRate(performanceReportDO.getPerCustomerRate());
            subRegion.setThirdDeptId(performanceReportDO.getThirdDeptId());
            subRegionList.add(subRegion);
        }
        achieveReportListRes.setSubRegionList(subRegionList);
        return achieveReportListRes;
    }

    @Data
    public static class SubRegion{
        @ApiModelProperty("门店名")
        private String storeName;
        //
        @ApiModelProperty("总销售额（业绩）")
        private BigDecimal grossSales;

        //
        @ApiModelProperty("销售额达成率")
        private BigDecimal grossSalesRate;

        //
        @ApiModelProperty("单产")
        private BigDecimal output;

        //单产达成率
        @ApiModelProperty("单产达成率")
        private BigDecimal outputRate;

        //销量
        @ApiModelProperty("销量")
        private BigDecimal salesVolume;
        //
        @ApiModelProperty("销量达成率")
        private BigDecimal salesVolumeRate;

        //
        @ApiModelProperty("客单价")
        private BigDecimal perCustomer;
        //
        @ApiModelProperty("客单价达成率")
        private BigDecimal perCustomerRate;

        private String thirdDeptId;

    }



}
