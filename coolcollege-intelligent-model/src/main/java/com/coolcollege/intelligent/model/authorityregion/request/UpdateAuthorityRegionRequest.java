package com.coolcollege.intelligent.model.authorityregion.request;

import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:35
 * @Description:
 */
@ApiModel("更新授权区域")
@Data
public class UpdateAuthorityRegionRequest {

    @NotNull(message = "id不能为空")
    @ApiModelProperty("授权区域id")
    private Long authorityRegionId;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("授权区域名称")
    private String name;

    @NotEmpty(message = "指定人员不能为空")
    @ApiModelProperty("指定人员")
    private List<String> userIds;

    @NotEmpty(message = "指定人员名称不能为空")
    @ApiModelProperty("指定人员名称不能为空")
    private List<String> userNames;

    public static AuthorityRegionDO convert(UpdateAuthorityRegionRequest param, String userName) {
        String userIds = String.join(",", param.getUserIds());
        String userNames = String.join(",", param.getUserNames());
        return AuthorityRegionDO.builder()
                .id(param.getAuthorityRegionId())
                .name(param.getName()).userIds(userIds).userNames(userNames)
                .updateName(userName).updateTime(new Date())
                .build();
    }
}
