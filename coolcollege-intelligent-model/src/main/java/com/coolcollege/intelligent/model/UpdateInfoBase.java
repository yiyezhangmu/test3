package com.coolcollege.intelligent.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 新增修改基础类
 * </p>
 *
 * @author wangff
 * @since 2025/3/10
 */
@Data
public class UpdateInfoBase {
    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("更新人id")
    private String updateUserId;

    @ApiModelProperty("更新人名称")
    private String updateUserName;
}
