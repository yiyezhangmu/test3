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
public class RelatedBusinessDTO {

    @ApiModelProperty("关联业务code")
    private String relatedBizCode;

    @ApiModelProperty("关联业务名称")
    private String relatedBizName;
}
