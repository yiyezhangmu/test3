package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/10/20 14:57
 * @Version 1.0
 */
@Data
public class StoreWorkPictureCenterColumnVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("图片路径")
    private String pictureUrl;
    @ApiModelProperty("视频路径")
    private String videoUrl;
    @ApiModelProperty("检查项ID")
    private Long metaColumnId;
    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    private String commentContent;

    private String commentUser;



}
