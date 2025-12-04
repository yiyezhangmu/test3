package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 店务定义项信息
 * @author wxp
 * @date 2022-09-08 10:48
 */
@ApiModel
@Data
public class StoreWorkColumnInfoRequest {
    /**
     * 检查项id
     */
    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty(value = "执行要求")
    private List<Boolean> executeDemand;

}
