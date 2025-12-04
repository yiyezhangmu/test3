package com.coolcollege.intelligent.model.usergroup.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel(value = "分组创建")
@Data
public class UserGroupAddRequest {

    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty(value = "分组名称", required = true)
    @NotBlank(message = "分组名称不能为空")
    @Length(max = 100, message = "名称最多100个字")
    private String groupName;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    @ApiModelProperty("配置用户userId集合")
    private List<String> userIdList;

}
