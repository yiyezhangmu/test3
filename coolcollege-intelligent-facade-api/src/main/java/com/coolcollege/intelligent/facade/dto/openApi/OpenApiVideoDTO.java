package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 17:38
 * @Description:
 */
@Data
public class OpenApiVideoDTO {

    /**
     * 设备通道号
     */
    private String channelNo;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 萤石云流播放协议，1-ezopen、2-hls、3-rtmp、4-flv，默认为1
     */
    private String protocol;

    /**
     * 视频清晰度，1-高清（主码流）、2-流畅（子码流）	(不传入默认是1) 对接的各个平台 如果有三个清晰度取最高和最低
     */
    private Integer quality;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 倍数播放 0.25、0.5、1、2、4倍速
     */
    private String speed;

    private String  supportH265;

    public boolean check() {
        return !StringUtils.isAnyBlank(channelNo, deviceId, protocol) && quality != null;
    }
}
