package com.coolcollege.intelligent.model.tbdisplay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author wxp
 */
@Data
public class TbDisplayHandlePhotoParam {
    /**
     * 检查表数据项id
     */
    @NotNull(message = "检查项id不能为空")
    private Long dataColumnId;

    /**
     * 图片地址
     */
    private String photoUrl;

    /**
     * 图片地址
     */
    private String photoArray;

    // 处理描述
    private String description;

    /**
     * 检查项上传的视频
     */
    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;

}
