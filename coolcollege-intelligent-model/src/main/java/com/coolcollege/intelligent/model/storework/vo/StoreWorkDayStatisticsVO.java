package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
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
public class StoreWorkDayStatisticsVO {

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    @Excel(name = "日期",format = "yyyy.MM.dd" ,orderNum = "0" ,width = 20)
    private Date storeWorkDate;

    @ApiModelProperty("完成率（该区域的完成率。已完成门店/总门店*100%）")
    @Excel(name = "完成率",orderNum = "1", numFormat = "#.##%")
    private BigDecimal finishPercent;

    @ApiModelProperty("应完成门店数")
    @Excel(name = "应完成门店数",orderNum = "2")
    private Integer totalStoreNum;

    @ApiModelProperty("未完成门店数")
    @Excel(name = "未完成门店数",orderNum = "3")
    private Integer unFinishStoreNum;

    @ApiModelProperty("已完成门店数")
    @Excel(name = "已完成门店数",orderNum = "4")
    private Integer finishStoreNum;

    @ApiModelProperty("平均合格率")
    @Excel(name = "平均合格率",orderNum = "5", numFormat = "#.##%")
    private BigDecimal avgPassRate;

    @ApiModelProperty("平均得分")
    @Excel(name = "平均得分",orderNum = "6")
    private BigDecimal avgScore;

    @ApiModelProperty("平均得分率")
    @Excel(name = "平均得分率",orderNum = "7", numFormat = "#.##%")
    private BigDecimal avgScoreRate;

    @ApiModelProperty("子工单数量")
    @Excel(name = "工单数量",orderNum = "8")
    private Integer questionNum;

    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数",orderNum = "9")
    private Integer failColumnNum;

    @ApiModelProperty("合格项数")
    private Integer passColumnNum;


}
