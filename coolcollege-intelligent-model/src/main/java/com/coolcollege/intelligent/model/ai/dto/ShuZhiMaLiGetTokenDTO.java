package com.coolcollege.intelligent.model.ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShuZhiMaLiGetTokenDTO {

    @ApiModelProperty("token")
    private String accessToken;

    @ApiModelProperty("token类型")
    private String tokenType;

    @ApiModelProperty("过期时间 单位秒")
    private int expires;
}
