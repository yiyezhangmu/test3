package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDetailDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:30
 * @Description:
 */

@Data
public class UpdateGroupConfigRequest {

    @NotNull(message = "群组id不能为空")
    @ApiModelProperty("群组id")
    private Long groupId;

    @NotBlank(message = "推送组名称不能为空")
    @ApiModelProperty("推送组名称")
    private String groupName;

    @NotBlank(message = "推送地址不能为空")
    @ApiModelProperty("推送地址")
    private String pushAddress;

    @NotEmpty(message = "用户不能为空")
    @ApiModelProperty("用户")
    private List<String> userIds;

    public TbWxGroupConfigDO convert(String userId) {
        return TbWxGroupConfigDO.builder()
                .id(groupId).groupName(groupName).pushAddress(pushAddress)
                .updateUserId(userId).updateTime(new Date())
                .build();
    }

    public List<TbWxGroupConfigDetailDO> convertListDetail(Long groupId, String userId, List<String> userIds) {
        List<TbWxGroupConfigDetailDO> list = new ArrayList<>(userIds.size());
        userIds.forEach(u -> list.add(TbWxGroupConfigDetailDO.builder()
                .groupId(groupId).userId(u)
                .pushAddress(pushAddress)
                .createUserId(userId).updateUserId(userId)
                .build())
        );
        return list;
    }
}
