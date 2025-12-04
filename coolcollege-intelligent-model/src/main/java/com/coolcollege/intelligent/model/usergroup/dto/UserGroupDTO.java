package com.coolcollege.intelligent.model.usergroup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户分组dto
 * @ClassName: UserGroupDTO
 * @Author: wxp
 * @Date: 2023/1/5 16:04
 */
@Data
public class UserGroupDTO {

    @ApiModelProperty("组别id")
    private String groupId;

    @ApiModelProperty("组别名称")
    private String groupName;




}
