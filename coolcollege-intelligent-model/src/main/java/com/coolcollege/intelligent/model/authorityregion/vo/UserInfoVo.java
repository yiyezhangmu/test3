package com.coolcollege.intelligent.model.authorityregion.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: hu hu
 * @Date: 2024/12/3 10:05
 * @Description:
 */
@Data
@Builder
public class UserInfoVo {

    @ApiModelProperty("人员id")
    private String userId;

    @ApiModelProperty("人员名称")
    private String userName;
}
