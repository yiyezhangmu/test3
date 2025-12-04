package com.coolcollege.intelligent.model.usergroup.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel(value = "分组删除")
@Data
public class UserGroupDeleteRequest {

    @ApiModelProperty(value = "分组id", required = true)
    @NotBlank(message = "分组id不能为空")
    private String groupId;

    @ApiModelProperty("userId集合")
    private List<String> userIdList;

}
