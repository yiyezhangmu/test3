package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/28 18:02
 * @Version 1.0
 */
@Data
public class HandlerUserVO {

    @ApiModelProperty("执行人ID ")
    private String userId;

    @ApiModelProperty("执行人名称 ")
    private String userName;

    @ApiModelProperty("执行人电话 ")
    private String userMobile;

    @ApiModelProperty("执行人优先级最高的角色对象")
    private List<SysRoleDO> userRoles;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("工号")
    private String jobnumber;

}
