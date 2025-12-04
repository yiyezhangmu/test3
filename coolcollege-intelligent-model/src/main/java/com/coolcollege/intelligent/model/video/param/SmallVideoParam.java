package com.coolcollege.intelligent.model.video.param;

import lombok.Data;

import java.util.Date;

/**
 * @author chenyupeng
 * @since 2021/10/11
 */
@Data
public class SmallVideoParam {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 任务id
     */
    private Long businessId;

    /**
     * 转码视频videoId
     */
    private String videoId;

    /**
     * @see com.coolcollege.intelligent.common.enums.video.UploadTypeEnum
     * 上传类型
     */
    private Integer uploadType;

    /**
     * 上传时间
     */
    private Date uploadTime;
}
