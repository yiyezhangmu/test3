package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class JosinyWeeklyNewspaperDetailVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("所在周的周一 yyyy-MM-dd")
    private String beginDate;

    @ApiModelProperty("所在周的周日 yyyy-MM-dd")
    private String endDate;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("总结")
    private String summary;

    @ApiModelProperty("下一周计划")
    private String nextWeekPlan;

    @ApiModelProperty("竞品收集")
    private String competeProductCollect;

    @ApiModelProperty("提交时间")
    private Date createTime;

    @ApiModelProperty("附件url")
    private String fileUrl;

    @ApiModelProperty("已读人数")
    private Integer readNum;

    @ApiModelProperty("区域id")
    private Long regionId;

}
