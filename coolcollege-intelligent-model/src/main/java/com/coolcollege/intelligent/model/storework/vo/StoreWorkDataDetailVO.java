package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import com.coolcollege.intelligent.model.patrolstore.statistics.TenRegionExportDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDataDetailVO extends TenRegionExportDTO {

    @ApiModelProperty("店务记录表tc_business_id")
    private String tcBusinessId;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private String storeWorkDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称",orderNum = "0")
    private String storeName;

    @ApiModelProperty("门店地址")
    private String storeAddress;

    @ApiModelProperty("门店编号")
    @Excel(name = "门店编号",orderNum = "1")
    private String storeNum;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域",orderNum = "2")
    private String fullRegionName;

    private List<String> regionNameList;

    @ApiModelProperty("完成率（已完成项/门店所需作业数）")
    @Excel(name = "完成率",orderNum = "3", numFormat = "#.##%")
    private BigDecimal finishPercent;

    @ApiModelProperty("应完成项")
    @Excel(name = "应完成项",orderNum = "4")
    private Integer totalColumnNum;

    @ApiModelProperty("未完成项")
    @Excel(name = "未完成项",orderNum = "5")
    private Integer unFinishColumnNum;

    @ApiModelProperty("已完成项")
    @Excel(name = "已完成项",orderNum = "6")
    private Integer finishColumnNum;

    @ApiModelProperty("平均合格率")
    @Excel(name = "平均合格率",orderNum = "7", numFormat = "#.##%")
    private BigDecimal avgPassRate;

    @ApiModelProperty("平均得分")
    @Excel(name = "平均得分",orderNum = "8")
    private BigDecimal avgScore;

    @ApiModelProperty("平均得分率")
    @Excel(name = "平均得分率",orderNum = "9", numFormat = "#.##%")
    private BigDecimal avgScoreRate;

    @ApiModelProperty("工单数")
    @Excel(name = "工单数",orderNum = "10")
    private Integer questionNum;

    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数",orderNum = "11")
    private Integer failColumnNum;

    @ApiModelProperty("合格项数")
    @Excel(name = "合格项数",orderNum = "12")
    private Integer passColumnNum;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    @Excel(name = "点评状态" ,orderNum = "13", replace = {"未点评_0","已点评_1"})
    private Integer commentStatus;

    @ApiModelProperty("完成执行时间 随表处理时间变化")
    @Excel(name = "完成执行时间",format = "yyyy-MM-dd HH:mm" ,orderNum = "14" ,width = 20)
    private Date endHandleTime;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty(value = "门店区域ID")
    private Long storeRegionId;

}
