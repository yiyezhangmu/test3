package com.coolcollege.intelligent.model.authorityregion.vo;

import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: huhu
 * @Date: 2024/11/25 16:44
 * @Description:
 */
@Data
@Builder
public class AuthorityRegionVO {

    @ApiModelProperty("授权区域id")
    private Long authorityRegionId;

    @ApiModelProperty("授权区域名称")
    private String name;

    @ApiModelProperty("指定人员")
    private List<String> userIds;

    @ApiModelProperty("指定人员")
    private List<String> userNames;

    @ApiModelProperty("指定人员")
    private List<UserInfoVo> userList;

    public static AuthorityRegionVO convert(AuthorityRegionDO authorityRegionDO) {
        List<String> userIdList = Lists.newArrayList(authorityRegionDO.getUserIds().split(","));
        List<String> userNameList = Lists.newArrayList(authorityRegionDO.getUserNames().split(","));
        List<UserInfoVo> userList = Lists.newArrayList();
        for (int i = 0; i < userIdList.size(); i++) {
            UserInfoVo userInfoVo = UserInfoVo.builder()
                    .userId(userIdList.get(i))
                    .userName(userNameList.get(i))
                    .build();
            userList.add(userInfoVo);
        }
        return AuthorityRegionVO.builder()
                .authorityRegionId(authorityRegionDO.getId())
                .name(authorityRegionDO.getName())
                .userIds(userIdList)
                .userNames(userNameList)
                .userList(userList)
                .build();
    }
}
