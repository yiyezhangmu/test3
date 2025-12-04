package com.coolcollege.intelligent.model.patrolstore.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-11-27 01:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreCloudDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createId;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("云图片")
    private String pics;

    @ApiModelProperty("云视频")
    private String video;
}