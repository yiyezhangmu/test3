package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:场景类型
 *
 * @author zhouyiping
 * @date 2021/10/18
 */
public enum SceneTypeEnum {

    /**
     * store_in,store_out,store_in_out,nothing
     */
    STORE_IN("store_in", "进店客流"),
    STORE_OUT("store_out", "出店客流"),
    STORE_IN_OUT("store_in_out", "进店+出店客流"),
    NOTHING("nothing", "其他");

    private String code;
    private String msg;

    protected static final Map<String, SceneTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(SceneTypeEnum::getCode, Function.identity()));

    SceneTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static SceneTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
