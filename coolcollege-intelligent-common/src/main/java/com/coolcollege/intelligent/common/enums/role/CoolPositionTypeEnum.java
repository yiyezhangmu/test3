package com.coolcollege.intelligent.common.enums.role;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/08
 */
public enum CoolPositionTypeEnum {

    /**
     * 店内职位
     */
    STORE_INSIDE("store_inside","店内职位"),
    /**
     * 店外职位
     */
    STORE_OUTSIDE("store_outside","店外职位");
    private String code;
    private String msg;

    protected static final Map<String, CoolPositionTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(CoolPositionTypeEnum::getCode, Function.identity()));

    CoolPositionTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static CoolPositionTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
