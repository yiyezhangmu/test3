package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/10/11 15:12
 * @Version 1.0
 */
@Data
public class StoreWorkTableVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("businessId")
    private String businessId;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("开始结束时间")
    @Excel(name = "检查时间",orderNum = "1")
    private String beginEndTime;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    @Excel(name = "检查表名称",orderNum = "2")
    private String metaTableName;

    @ApiModelProperty("表设置信息")
    private String tableInfo;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称",orderNum = "3")
    private String storeName;

    @ApiModelProperty("门店编号")
    @Excel(name = "门店编号",orderNum = "4")
    private String storeNum;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域",orderNum = "5")
    private String allRegionName;

    @ApiModelProperty("完成状态")
    @Excel(name = "完成状态",orderNum = "6",replace = {"未完成_0","已完成_1"})
    private Integer completeStatus;

    @ApiModelProperty("合格项数")
    @Excel(name = "合格项数",orderNum = "7")
    private Integer passColumnNum;

    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数",orderNum = "8")
    private Integer failColumnNum;

    @ApiModelProperty("门店得分")
    @Excel(name = "门店得分",orderNum = "9")
    private BigDecimal score;

    @ApiModelProperty("合格率")
    @Excel(name = "合格率",orderNum = "10")
    private String passRate;

    @ApiModelProperty("得分率")
    @Excel(name = "得分率",orderNum = "11")
    private String scoreRate;

    @ApiModelProperty("采集项数")
    private Integer collectColumnNum;

    @ApiModelProperty("日清名称")
    @Excel(name = "日清名称",orderNum = "12")
    private String storeWorkName;

    @ApiModelProperty("开始执行时间")
    @Excel(name = "开始执行时间",format = "yyyy-MM-dd HH:mm:ss",orderNum = "13")
    private Date beginHandleTime;

    @ApiModelProperty("完成执行时间 随表处理时间变化")
    @Excel(name = "完成执行时间",format = "yyyy-MM-dd HH:mm:ss",orderNum = "14")
    private Date endHandleTime;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    @Excel(name = "点评状态" ,orderNum = "15", replace = {"未点评_0","已点评_1"})
    private Integer commentStatus;

    @ApiModelProperty("执行人id")
    private String actualHandleUserId;

    @ApiModelProperty("执行人姓名")
    @Excel(name = "执行人姓名",orderNum = "16")
    private String actualHandleUserName;
    @ApiModelProperty("storeWorkDate")
    private Date storeWorkDate;
}
