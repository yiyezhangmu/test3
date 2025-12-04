package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 14:02
 * @Description: 新增或更新云图库信息
 */
@ApiModel("新增或更新云图库信息")
@Data
public class AddPatrolStoreCloudRequest {

    @NotNull(message = "巡店记录id不能为空")
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("云图片")
    private String pics;

    @ApiModelProperty("云视频")
    private String video;

    public static TbPatrolStoreCloudDO convert(AddPatrolStoreCloudRequest param, CurrentUser currentUser) {
        return TbPatrolStoreCloudDO.builder()
                .businessId(param.getBusinessId())
                .pics(param.getPics()).video(param.getVideo())
                .createName(currentUser.getName()).createId(currentUser.getUserId())
                .deleted(false)
                .build();
    }
}
