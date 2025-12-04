package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author byd
 * @date 2025-10-14 14:47
 */
@Data
public class AiInspectionStatisticsPicListVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("抓拍日期(巡检日期)")
    private Date captureDate;

    @ApiModelProperty("巡检结果")
    private List<AiInspectionStatisticsPicDetailVO> imageList;

}
