package com.coolcollege.intelligent.common.enums.video;

/**
 * video status
 */
public enum ResourceStatusEnum {

    UPLOADING(0),// 上传中

    TRANSCODING(1),// 转码中

    SCREENSHOT_FINISHED(2),//截图完成

    TRANSCODE_FINISH(3),//完成

    TRANSCODE_FAILED(4);//失败


    private final Integer value;

    ResourceStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
