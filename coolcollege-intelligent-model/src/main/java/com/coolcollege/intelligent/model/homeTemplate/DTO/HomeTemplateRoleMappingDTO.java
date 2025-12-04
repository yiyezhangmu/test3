package com.coolcollege.intelligent.model.homeTemplate.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/6/27 14:40
 * @Version 1.0
 */
@ApiModel
@Data
public class HomeTemplateRoleMappingDTO {

    /**
     * 模板ID
     */
    @ApiModelProperty("模板ID")
    private Integer templateId;

    /**
     *角色ID
     */
    @ApiModelProperty("角色ID")
    private Long roleId;
}
