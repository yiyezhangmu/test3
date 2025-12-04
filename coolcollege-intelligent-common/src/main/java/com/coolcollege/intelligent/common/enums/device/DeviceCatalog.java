package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/05/06
 */
public enum DeviceCatalog {

    /**
     * test
     */
    IPC("IPC", "IPC摄像头"),
    NVR("NVR","网络录像机设备"),
    DVR("DVR","有线录像机设备");



    private String code;
    private String msg;

    protected static final Map<String, DeviceCatalog> map = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceCatalog::getCode, Function.identity()));

    DeviceCatalog(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static DeviceCatalog getByCode(String code) {
        return map.get(code);
    }

}
