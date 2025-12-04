package com.coolcollege.intelligent.model.usergroup.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author wxp
 */
@ApiModel(value = "分组创建")
@Data
public class UserGroupRemoveRequest {

    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty("移除用户userId集合")
    private List<String> userIdList;

}
