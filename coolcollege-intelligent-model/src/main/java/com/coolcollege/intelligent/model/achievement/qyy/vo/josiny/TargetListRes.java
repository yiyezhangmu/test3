package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import com.coolcollege.intelligent.model.qyy.josiny.QyyTargetDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TargetListRes {

    /**
     *
     */
    @ApiModelProperty("单产目标")
    private BigDecimal unitYieldTarget;
    /**
     *
     */
    @ApiModelProperty("业绩目标")
    private BigDecimal goalAmt;
    /**
     *
     */
    @ApiModelProperty("销量目标")
    private BigDecimal salesTarget;

    @ApiModelProperty("门店名")
    private String regionName;

    private Date updateTime;

    @ApiModelProperty("子列表")
    private List<RegionTarget> regionTargetList;

    public static TargetListRes convert(QyyTargetDO qyyTargetDO,List<QyyTargetDO> qyyTargetList) {
        if (qyyTargetDO == null) {
            return null;
        }
        TargetListRes targetListRes = new TargetListRes();
        targetListRes.setUnitYieldTarget(qyyTargetDO.getUnitYieldTarget());
        targetListRes.setGoalAmt(qyyTargetDO.getGoalAmt());
        targetListRes.setSalesTarget(qyyTargetDO.getSalesTarget());
        targetListRes.setRegionName(qyyTargetDO.getStoreName());
        targetListRes.setUpdateTime(qyyTargetDO.getUpdateTime());
        List<RegionTarget> regionTargetList = new ArrayList<>();
        for (QyyTargetDO targetDO : qyyTargetList) {
            RegionTarget regionTarget = new RegionTarget();
            regionTarget.setGoalAmt(targetDO.getGoalAmt());
            regionTarget.setRegionName(targetDO.getStoreName());
            regionTarget.setSalesTarget(targetDO.getSalesTarget());
            regionTarget.setUnitYieldTarget(targetDO.getUnitYieldTarget());
            regionTargetList.add(regionTarget);
        }
        if (CollectionUtils.isNotEmpty(regionTargetList) || regionTargetList.size() > 0){
            targetListRes.setRegionTargetList(regionTargetList);
        }
        return targetListRes;
    }




    @Data
    public static class RegionTarget{
        @ApiModelProperty("门店名")
        private String regionName;
        /**
         *
         */
        @ApiModelProperty("单产目标")
        private BigDecimal unitYieldTarget;
        /**
         *
         */
        @ApiModelProperty("业绩目标")
        private BigDecimal goalAmt;
        /**
         *
         */
        @ApiModelProperty("销量目标")
        private BigDecimal salesTarget;

    }




}
