package com.coolcollege.intelligent.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 文件DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/28
 */
@Data
public class FileDTO {
    @ApiModelProperty("url")
    private String url;

    @ApiModelProperty("缩略图")
    private String thumbUrl;

    @ApiModelProperty("文件类型")
    private String fileType;
}
