package com.coolcollege.intelligent.model.login.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: FeiShuLoginDTO
 * @Description:
 * @date 2022-11-17 19:25
 */
@Data
public class FeiShuLoginDTO {

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("应用id")
    private String appId;

}
