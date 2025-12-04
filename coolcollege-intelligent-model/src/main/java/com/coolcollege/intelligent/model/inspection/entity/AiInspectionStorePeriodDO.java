package com.coolcollege.intelligent.model.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI巡检门店抓拍周期表
 * @author   zhangchenbiao
 * @date   2025-10-14 03:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionStorePeriodDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("ai巡检表id")
    private Long inspectionId;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("抓拍日期")
    private Date captureDate;

    @ApiModelProperty("抓拍日期")
    private Date captureTime;

    @ApiModelProperty("AI轮次检测结果：PASS-合格, FAIL-不合格, INAPPLICABLE-不适用")
    private String aiPeriodResult;

    @ApiModelProperty("抓拍这周周一的日期")
    private Date weekDay;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}