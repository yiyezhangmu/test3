package com.coolcollege.intelligent.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author byd
 * @date 2025-10-09 17:17
 */
@Data
public class IdRequest {

    @ApiModelProperty("id")
    @NotNull
    private Long id;

}
