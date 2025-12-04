package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2023-04-17 16:14
 */
@ApiModel
@Data
public class CheckRuleDTO {

    @ApiModelProperty("核验规则code")
    private String checkRuleCode;

    @ApiModelProperty("核验规则名称")
    private String checkRuleName;
}
