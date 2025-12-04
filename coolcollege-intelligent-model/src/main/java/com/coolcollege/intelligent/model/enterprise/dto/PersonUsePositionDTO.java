package com.coolcollege.intelligent.model.enterprise.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author byd
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonUsePositionDTO {
    /**
     * id
     */
    @ApiModelProperty("value")
    private String value;
    /**
     * 名字
     */
    @ApiModelProperty("名称")
    private String name;
    /**
     * 类型 person:人员   position:岗位
     */
    @ApiModelProperty("类型 person:人员   position:岗位 userGroup:用户分组  organization:组织架构")
    private String type;
}
