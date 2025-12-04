package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum YingShiCloudMsgTypeEnum {

    VIDEO_FRAME_STATUS_CHANGE("video_frame_status_change", "抽帧状态变更，抽到图片"),
    VIDEO_FRAME("video_frame", "抽帧结束"),
    ;

    private String code;
    private String msg;

    protected static final Map<String, YingShiCloudMsgTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(YingShiCloudMsgTypeEnum::getCode, Function.identity()));

    YingShiCloudMsgTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static YingShiCloudMsgTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
