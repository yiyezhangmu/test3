package com.coolcollege.intelligent.model.boss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/4/8 10:37
 */
@ApiModel("管理员boss超登记录")
@Data
public class AdminLoginEnterpriseDTO {

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("企业id")
    @NotEmpty(message = "企业id不能为空")
    private String enterpriseId;

}