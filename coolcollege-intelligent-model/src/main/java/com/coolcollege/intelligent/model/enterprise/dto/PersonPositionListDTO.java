package com.coolcollege.intelligent.model.enterprise.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author byd
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonPositionListDTO {

    /**
     * id
     */
    @ApiModelProperty("人员列表")
    private List<PersonPositionDTO> peopleList;

    /**
     * 类型 person:人员   position:岗位
     */
    @ApiModelProperty("类型 person:人员   position:岗位")
    private String type;

    @ApiModelProperty("工单发起人审批 0 不可以 1 可以")
    private Boolean createUserApprove;

}
