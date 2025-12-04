package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.patrolstore.statistics.TenRegionExportDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkStatisticsOverviewVO extends TenRegionExportDTO {

    /**
     * 排名
     */
    @Excel(name = "排名",orderNum = "-1")
    private Integer rank;

    @ApiModelProperty(value = "区域ID")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    @Excel(name = "区域名称",orderNum = "0")
    private String regionName;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域",orderNum = "1")
    private String fullRegionName;

    @ApiModelProperty("平均完成率（所有门店的完成率之和/应完成门店数量）")
    @Excel(name = "平均完成率",orderNum = "2" , numFormat = "#.##%")
    private BigDecimal finishPercent;

    @ApiModelProperty("应完成门店 该区域下的门店数")
    @Excel(name = "应完成门店数",orderNum = "3")
    private Long totalStoreNum;

    @ApiModelProperty("未完成门店")
    @Excel(name = "未完成门店",orderNum = "5")
    private Long unFinishStoreNum;

    @ApiModelProperty("已完成门店")
    @Excel(name = "已完成门店数",orderNum = "4")
    private Long finishStoreNum;

    @ApiModelProperty("平均合格率 门店合格率之和/已经点评的门店数")
    @Excel(name = "平均合格率",orderNum = "6", numFormat = "#.##%")
    private BigDecimal avgPassRate;

    @ApiModelProperty("平均得分 门店得分之和/已经点评的门店数量")
    @Excel(name = "平均得分",orderNum = "7")
    private BigDecimal avgScore;

    @ApiModelProperty("平均得分率 门店得分率之和/已经点评的门店数量")
    @Excel(name = "平均得分率",orderNum = "8", numFormat = "#.##%")
    private BigDecimal avgScoreRate;

    @ApiModelProperty("平均点评率 已完成点评的门店/应完成门店数*100%")
    private BigDecimal avgCommentRate;

    @ApiModelProperty("工单数 店务工单类型产生的子工单数")
    @Excel(name = "工单数",orderNum = "9")
    private Long questionNum;

    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数",orderNum = "10")
    private Long failColumnNum;

    @ApiModelProperty("合格项数")
    @Excel(name = "合格项数",orderNum = "11")
    private Long passColumnNum;

    @ApiModelProperty("日期")
    private String storeWorkDate;
}
