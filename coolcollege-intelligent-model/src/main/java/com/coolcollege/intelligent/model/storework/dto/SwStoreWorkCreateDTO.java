package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/3/24 14:00
 * @Version 1.0
 */
@ApiModel
@Data
public class SwStoreWorkCreateDTO {

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("是否可以补发 true 是 false 否")
    private Boolean canReissue;

}
