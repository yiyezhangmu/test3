package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/19 14:36
 * @Version 1.0
 */
@Data
public class VideoFileDTO {

    private String fileId;

    //0正常 1上传中 2上传失败
    private Integer status;

    private Integer fileCount;

    private Long fileSize;

    private Long duration;

    private String errorCode;

    private String createTime;

    private String errorMsg;

    public VideoFileDTO() {
    }

    public VideoFileDTO(Integer status) {
        this.status = status;
    }
}
