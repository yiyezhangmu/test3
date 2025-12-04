package com.coolcollege.intelligent.model.homepage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: TableAverageScoreVO
 * @Description: 表的平均分
 * @date 2022-06-22 11:22
 */
@Data
public class TableAverageScoreVO {

    @ApiModelProperty("区域")
    private List<TableAverageScore> regionTableAvgList;

    @ApiModelProperty("门店")
    private List<TableAverageScore> storeTableAvgList;

    public TableAverageScoreVO(List<TableAverageScore> regionTableAvgList, List<TableAverageScore> storeTableAvgList) {
        this.regionTableAvgList = regionTableAvgList;
        this.storeTableAvgList = storeTableAvgList;
    }

    @Data
    public static class TableAverageScore{
        @ApiModelProperty("区域Id")
        private Long regionId;

        @ApiModelProperty("区域名称")
        private String regionName;

        @ApiModelProperty("区域类型 root 根目录  path 区域 leaf 门店")
        private String regionType;

        @ApiModelProperty("检查表id")
        private Long metaTableId;

        @ApiModelProperty("检查表名称")
        private String tableName;

        @ApiModelProperty("已巡门店数量")
        private Long patrolStoreNum;

        @ApiModelProperty("巡店次数")
        private Long patrolNum;

        @ApiModelProperty("完成数量")
        private Long finishNum;

        @ApiModelProperty("平均分")
        private BigDecimal avgScore;

        @ApiModelProperty("巡检得分率")
        private BigDecimal scorePercent;

        @ApiModelProperty("比较信息")
        private TableAverageScoreCompare compareInfo;
    }

    @Data
    public static class TableAverageScoreCompare{

        @ApiModelProperty("已巡门店数量")
        private Long patrolStoreNum;

        @ApiModelProperty("巡店次数")
        private Long patrolNum;

        @ApiModelProperty("完成数量")
        private Long finishNum;

        @ApiModelProperty("平均分")
        private BigDecimal avgScore;

        @ApiModelProperty("巡检得分率")
        private BigDecimal scorePercent;
    }

}
