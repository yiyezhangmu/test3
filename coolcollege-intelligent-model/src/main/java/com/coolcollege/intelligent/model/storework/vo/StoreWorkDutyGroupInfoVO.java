package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 店务定义执行信息
 * @author wxp
 * @date 2022-09-08 10:48
 */
@ApiModel
@Data
public class StoreWorkDutyGroupInfoVO {

    @ApiModelProperty("分组排序")
    private Integer groupNum;

    @ApiModelProperty("执行人信息")
    private List<StoreWorkCommonDTO> handlePersonInfo;

    @ApiModelProperty("执行任务信息")
    private List<StoreWorkTableInfoVO> tableInfoList;

    @ApiModelProperty("点评人信息")
    private List<StoreWorkCommonDTO> commentPersonInfo;

}
