package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author byd
 * @date 2023-08-16 16:38
 */
@ApiModel
@Data
public class BatchDataColumnAppealDTO {

    @ApiModelProperty
    private List<DataColumnAppealDTO> dataColumnAppealList;
}
