package com.coolcollege.intelligent.model.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 * @date 2021-01-29 13:46
 */
@ApiModel("boss用户登陆返回实体")
@Data
public class BossLoginUserDTO implements Serializable {

    @ApiModelProperty("用户主键")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("酷店掌用户id")
    private String userId;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("是否需要修改密码")
    private String needChangePassword;
}
