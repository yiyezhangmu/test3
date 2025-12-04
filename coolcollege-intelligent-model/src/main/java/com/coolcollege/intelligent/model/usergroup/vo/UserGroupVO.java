package com.coolcollege.intelligent.model.usergroup.vo;

import com.coolcollege.intelligent.model.user.dto.UserSimpleDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserGroupVO {

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty("人员数量")
    private Integer userCount;

    private List<UserSimpleDTO> commonEditUserList;

    @ApiModelProperty("更新人")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("创建人")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("配置用户集合")
    private List<UserSimpleDTO> configUserList;

    @ApiModelProperty("编辑权限")
    private Boolean editFlag;

}
