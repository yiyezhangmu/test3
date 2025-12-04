package com.coolcollege.intelligent.model.inspection.vo;

import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesExtendInfo;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
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
public class AiInspectionStrategiesVO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("策略描述")
    private String description;

    @ApiModelProperty("状态:0-禁用,1-启用")
    private Integer status;

    @ApiModelProperty("定时执行日期例周一到周五执行“1,2,3,4,5”例每月1号17号执行“1,17”")
    private String runDate;

    @ApiModelProperty("抓拍设备场景 多个, 分隔")
    private String tags;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者ID")
    private String createUserId;
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人ID")
    private String updateUserId;

    @ApiModelProperty("门店范围")
    List<AiInspectionStoreMappingVO> storeMappingList;

    @ApiModelProperty("时间范围")
    List<AiInspectionTimePeriodVO> timePeriodList;

    @ApiModelProperty("场景")
    List<StoreSceneVO> storeSceneList;

    @ApiModelProperty("配置信息")
    private AiInspectionStrategiesExtendInfo extendInfoConfig;

}