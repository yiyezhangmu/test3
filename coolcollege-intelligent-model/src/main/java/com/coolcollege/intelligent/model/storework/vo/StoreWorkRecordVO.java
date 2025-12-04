package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class StoreWorkRecordVO {

    @ApiModelProperty("店务记录表tc_business_id")
    private String tcBusinessId;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private Date storeWorkDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称",orderNum = "0")
    private String storeName;

    @ApiModelProperty("门店编号")
    @Excel(name = "门店编号",orderNum = "1")
    private String storeNum;

    private String regionPath;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域",orderNum = "2")
    private String fullRegionName;

    @ApiModelProperty("店务名称")
    @Excel(name = "店务名称",orderNum = "3")
    private String workName;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    @Excel(name = "完成状态" ,orderNum = "4", replace = {"未完成_0","已完成_1"})
    private Integer completeStatus;

    @ApiModelProperty("开始执行时间")
    @Excel(name = "开始执行时间",format = "yyyy-MM-dd HH:mm",orderNum = "5")
    private Date beginHandleTime;

    @ApiModelProperty("完成执行时间 随表处理时间变化")
    @Excel(name = "完成执行时间",format = "yyyy-MM-dd HH:mm",orderNum = "6")
    private Date endHandleTime;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    @Excel(name = "点评状态" ,orderNum = "7", replace = {"未点评_0","已点评_1"})
    private Integer commentStatus;

    @ApiModelProperty("合格项数")
    @Excel(name = "合格项数",orderNum = "8")
    private Integer passColumnNum;

    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数",orderNum = "9")
    private Integer failColumnNum;

    @ApiModelProperty("门店得分")
    @Excel(name = "门店得分",orderNum = "10")
    private BigDecimal totalGetScore;

    @ApiModelProperty("平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("合格率")
    @Excel(name = "合格率",orderNum = "11", numFormat = "#.##%")
    private String avgPassRate;

    @ApiModelProperty("得分率")
    @Excel(name = "得分率",orderNum = "12", numFormat = "#.##%")
    private String avgScoreRate;

    @ApiModelProperty("执行人id")
    private String actualHandleUserId;

    @ApiModelProperty("执行人姓名")
    @Excel(name = "执行人姓名",orderNum = "13")
    private String actualHandleUserName;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

}
