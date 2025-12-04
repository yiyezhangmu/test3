package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author byd
 */
@ApiModel(value = "分享请求参数")
@Data
public class ShareSubQuery {

    /**
     * 分享key
     */
    @ApiModelProperty(value = "分享key", required = true)
    @NotBlank(message = "分享key不能为空")
    private String key;
}
