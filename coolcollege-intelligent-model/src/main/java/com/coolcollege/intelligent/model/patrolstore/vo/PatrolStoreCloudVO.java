package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 14:08
 * @Description: 云图库信息
 */
@Data
@Builder
public class PatrolStoreCloudVO {

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("云图片")
    private String pics;

    @ApiModelProperty("云视频")
    private String video;

    public static PatrolStoreCloudVO convert(TbPatrolStoreCloudDO param) {
        return PatrolStoreCloudVO.builder()
                .businessId(param.getBusinessId())
                .pics(param.getPics()).video(param.getVideo())
                .build();
    }
}
