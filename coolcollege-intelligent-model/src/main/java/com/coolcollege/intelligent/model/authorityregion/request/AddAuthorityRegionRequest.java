package com.coolcollege.intelligent.model.authorityregion.request;

import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:30
 * @Description: 新增授权区域
 */
@ApiModel("新增授权区域")
@Data
public class AddAuthorityRegionRequest {

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("授权区域名称")
    private String name;

    @NotEmpty(message = "指定人员不能为空")
    @ApiModelProperty("指定人员")
    private List<String> userIds;

    @NotEmpty(message = "指定人员名称不能为空")
    @ApiModelProperty("指定人员名称不能为空")
    private List<String> userNames;

    /**
     * 数据封装
     * @param param 授权区域
     * @param userName 人员名称
     * @return 授权区域
     */
    public static AuthorityRegionDO convert(AddAuthorityRegionRequest param, String userName) {
        String userIds = String.join(",", param.getUserIds());
        String userNames = String.join(",", param.getUserNames());
        return AuthorityRegionDO.builder()
                .name(param.getName()).userIds(userIds).userNames(userNames)
                .createName(userName).updateName(userName)
                .createTime(new Date()).updateTime(new Date())
                .build();
    }
}
