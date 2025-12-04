package com.coolcollege.intelligent.model.usergroup;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-12-28 07:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUserGroupDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("组别id")
    private String groupId;

    @ApiModelProperty("组别名称")
    private String groupName;

    @ApiModelProperty("共同编辑人userId集合（前后逗号分隔）")
    private String commonEditUserids;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}