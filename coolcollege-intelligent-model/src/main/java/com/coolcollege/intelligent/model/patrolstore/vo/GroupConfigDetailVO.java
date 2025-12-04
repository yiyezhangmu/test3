package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDetailDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @Author: huhu
 * @Date: 2024/9/6 15:11
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfigDetailVO {

    @ApiModelProperty("群组id")
    private Long groupId;

    @ApiModelProperty("推送组名称")
    private String groupName;

    @ApiModelProperty("推送地址")
    private String pushAddress;

    @ApiModelProperty("用户")
    private List<GroupUserVO> users;

    public static GroupConfigDetailVO covert(TbWxGroupConfigDO tbWxGroupConfigDO, Map<String, String> userNameMap, List<TbWxGroupConfigDetailDO> groupConfigDetailList) {
        List<GroupUserVO> users = new ArrayList<>();
        groupConfigDetailList.forEach(d -> {
            users.add(GroupUserVO.builder().userId(d.getUserId()).userName(userNameMap.get(d.getUserId())).build());
        });

        return GroupConfigDetailVO.builder()
                .groupId(tbWxGroupConfigDO.getId()).groupName(tbWxGroupConfigDO.getGroupName())
                .pushAddress(tbWxGroupConfigDO.getPushAddress()).users(users)
                .build();
    }
}
