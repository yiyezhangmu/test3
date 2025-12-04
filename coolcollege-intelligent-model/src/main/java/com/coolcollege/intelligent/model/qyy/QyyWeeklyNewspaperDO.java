package com.coolcollege.intelligent.model.qyy;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-04-12 03:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyWeeklyNewspaperDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @Excel(name = "周报时间",orderNum = "4")
    @ApiModelProperty("周一对应yyyy-MM-dd")
    private String mondayOfWeek;

    @Excel(name = "填写人",orderNum = "9")
    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("群id")
    private String conversationId;

    @Excel(name = "本周总结",orderNum = "6")
    @ApiModelProperty("总结")
    private String summary;

    @Excel(name = "下周计划",orderNum = "7")
    @ApiModelProperty("下周计划")
    private String nextWeekPlan;

    @Excel(name = "竞品收集",orderNum = "8")
    @ApiModelProperty("竞品收集")
    private String competeProductCollect;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间",orderNum = "10")
    private String updateTime;

    @ApiModelProperty("附件url")
    private String fileUrl;

    @Excel(name = "已读人数",orderNum = "3")
    @ApiModelProperty("已读数量")
    private Integer readNum;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @Excel(name = "门店名称",orderNum = "1")
    @ApiModelProperty("门店名称")
    private String storeName;

    @Excel(name = "所属分公司",orderNum = "2")
    @ApiModelProperty("分公司名称")
    private String compName;

    @Excel(name = "周业绩",orderNum = "5")
    @ApiModelProperty("周业绩")
    private String weekAchieve;

    @ApiModelProperty("三方唯一id")
    private String dingDeptId;

    @ApiModelProperty("视频json")
    private String videoUrl;

}