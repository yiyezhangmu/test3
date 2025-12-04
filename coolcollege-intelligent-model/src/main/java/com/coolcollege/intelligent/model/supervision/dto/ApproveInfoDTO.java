package com.coolcollege.intelligent.model.supervision.dto;

import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/4/10 16:32
 * @Version 1.0
 */
@Data
@ApiModel
public class ApproveInfoDTO {
    @ApiModelProperty("审批类型 1-自定义 2-按汇报线 ")
    private Integer approveType;
    @ApiModelProperty("自定义 填写一级审批")
    private List<GeneralDTO> firstApproveList;
    @ApiModelProperty("汇报线自定义的时候 填写审批层级")
    private Integer approveHierarchy;
}
