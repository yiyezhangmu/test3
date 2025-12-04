package com.coolcollege.intelligent.model.enums;

import com.coolstore.base.enums.VideoProtocolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 杰峰云视频格式
 * </p>
 *
 * @author wangff
 * @since 2025/5/7
 */
@Getter
@AllArgsConstructor
public enum JfyVideoProtocolTypeEnum {

    FLV("flv"),
    FLV_ENHANCED("flv-enhanced"),
    HLS_TS("hls-ts"),
    HLS_FMP4("hls-fmp4"),
    MP4("mp4"),
    WS_PRI("ws-pri"),
    WS_FLV("ws-flv"),
    WS_FLV_ENHANCED("ws-flv-enhanced"),
    RTSP_SDP("rtsp-sdp"),
    RTSP_PRI("rtsp-pri"),
    RTMP_FLV("rtmp-flv"),
    RTMP_ENHANCED("rtmp-enhanced"),
    WEBRTC("webrtc");

    private final String code;

    public static JfyVideoProtocolTypeEnum getByVideoProtocolTypeEnum(VideoProtocolTypeEnum protocolTypeEnum) {
        if (protocolTypeEnum == null) return MP4;
        switch (protocolTypeEnum) {
            case RTMP:
                return RTMP_FLV;
            case RTSP:
                return RTSP_SDP;
            case HLS:
                return HLS_TS;
            case FLV:
            case HTTP_FLV:
            case HTTPS_FLV:
                return FLV;
            default:
                return MP4;
        }
    }
}
