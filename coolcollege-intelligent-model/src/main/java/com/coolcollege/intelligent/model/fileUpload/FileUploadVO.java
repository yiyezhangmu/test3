package com.coolcollege.intelligent.model.fileUpload;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件上传vo
 * @author zhangnan
 * @date 2022-01-02 17:05
 */
@Data
public class FileUploadVO {

    /**
     * 文件ossObjectKey
     */
    @ApiModelProperty("文件ossObjectKey")
    private String ossObjectKey;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 文件上传路径
     */
    @ApiModelProperty("文件上传地址")
    private String uploadUrl;

    /**
     * 文件访问路径
     */
    @ApiModelProperty("文件访问地址")
    private String fileUrl;

}
