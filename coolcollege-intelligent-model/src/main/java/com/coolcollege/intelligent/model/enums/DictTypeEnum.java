package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/3/26 16:46
 */
public enum DictTypeEnum {
    MODEL_NAME_DEFINE("MODEL_NAME_DEFINE", "模块名称自定义",Boolean.TRUE);

    /**
     * 类型code
     */
    private String code;

    /**
     * 类型描述
     */
    private String describe;

    /**
     * 是否是提供给前端的字典类型
     */
    private Boolean isForUi;

    private static final Map<String, String> codeDescribeMap = Arrays.asList(DictTypeEnum.values()).stream()
            .collect(Collectors.toMap(data -> data.getCode(), data -> data.getDescribe(), (a, b) -> a));

    DictTypeEnum(String code, String describe,Boolean isForUi) {
        this.code = code;
        this.describe = describe;
        this.isForUi = isForUi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public static String getDescribeByCode(String code){
        return codeDescribeMap.get(code);
    }
}
