package com.coolcollege.intelligent.model.inspection.request;

import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesExtendInfo;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AI巡检策略表
 *
 * @author zhangchenbiao
 * @date 2025-09-25 04:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionStrategiesRequest implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("巡检场景id列表(添加接口)")
    private List<Long> sceneIdList;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("策略描述")
    private String description;

    @ApiModelProperty("定时执行日期例周一到周五执行“1,2,3,4,5”例每月1号17号执行“1,17”")
    private String runDate;

    @ApiModelProperty("抓拍设备场景 多个, 分隔")
    private String tags;

    @ApiModelProperty("门店范围")
    List<AiInspectionStoreMappingRequest> storeMappingList;

    @ApiModelProperty("时间范围")
    List<AiInspectionTimePeriodDTO> timePeriodList;

    @ApiModelProperty("配置信息")
    private AiInspectionStrategiesExtendInfo extendInfoConfig;

}