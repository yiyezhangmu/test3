package com.coolcollege.intelligent.model.homepage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: RegionPatrolVO
 * @Description: 区域统计
 * @date 2022-06-20 15:34
 */
@Data
public class PatrolRegionDataVO {

    @ApiModelProperty("区域巡店数据")
    private List<PatrolRegionData> regionPatrolList;

    @ApiModelProperty("门店巡店数据")
    private List<PatrolRegionData> storePatrolList;

    public PatrolRegionDataVO(List<PatrolRegionData> regionPatrolList, List<PatrolRegionData> storePatrolList) {
        this.regionPatrolList = regionPatrolList;
        this.storePatrolList = storePatrolList;
    }

    @Data
    public static class PatrolRegionData{
        @ApiModelProperty("区域Id")
        private Long regionId;

        @ApiModelProperty("区域名称")
        private String regionName;

        @ApiModelProperty("节点类型")
        private String regionType;

        @ApiModelProperty("总门店数量")
        private Long storeNum;

        @ApiModelProperty("总巡店次数")
        private Long patrolNum;

        @ApiModelProperty("巡店人数")
        private Long patrolPersonNum;

        @ApiModelProperty("已巡门店数量")
        private Long patrolStoreNum;

        @ApiModelProperty("合格数量")
        private Long passNum;

        @ApiModelProperty("任务数量")
        private Long taskNum;

        @ApiModelProperty("逾期数量")
        private Long overDueNum;

        @ApiModelProperty("平均得分")
        private BigDecimal avgScore;

        @ApiModelProperty("得分率")
        private BigDecimal scorePercent;

        @ApiModelProperty("巡店覆盖率")
        private BigDecimal storeCoverPercent;

        @ApiModelProperty("比较信息")
        private PatrolCompare compareInfo;
    }

    @Data
    public static class PatrolCompare{
        @ApiModelProperty("总门店数量")
        private Long storeNum;

        @ApiModelProperty("总巡店次数")
        private Long patrolNum;

        @ApiModelProperty("巡店人数")
        private Long patrolPersonNum;

        @ApiModelProperty("已巡门店数量")
        private Long patrolStoreNum;

        @ApiModelProperty("合格数量")
        private Long passNum;

        @ApiModelProperty("任务数量")
        private Long taskNum;

        @ApiModelProperty("逾期数量")
        private Long overDueNum;

        @ApiModelProperty("平均得分")
        private BigDecimal avgScore;

        @ApiModelProperty("得分率")
        private BigDecimal scorePercent;

        @ApiModelProperty("巡店覆盖率")
        private BigDecimal storeCoverPercent;
    }


}
