package com.coolcollege.intelligent.model.video;

import lombok.Data;

/**
 * 上传的视频
 *
 * @author chenyupeng
 * @since 2021/10/9
 */
@Data
public class SmallVideoDTO {

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 视频url
     */
    private String videoUrl;

    /**
     * 原视频url
     */
    private String videoUrlBefore;

    /**
     * 视频截图封面
     */
    private String videoSnapshot;

    /**
     * 视频大小
     */
    private Long size;

    /**
     * 转码状态
     * @see com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum
     */
    private Integer status;

    /**
     * 上传是否完成
     */
    private boolean uploadStatus = false;

    /**
     * 截图是否完成
     */
    private boolean snapShotStatus = false;

    /**
     * 转码是否完成
     */
    private boolean transCodeStatus = false;

}
