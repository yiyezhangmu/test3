package com.coolcollege.intelligent.service.jms.constans;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息图片钉钉地址枚举
 */
public enum PicUrlEnum {

    COMPLETE_STORE("https://oss-cool.coolstore.cn/notice_pic/46bde08f49614d8ba1fa238c07dde2d0.png"), // 门店补全
    PATROL_TASK("https://oss-cool.coolstore.cn/notice_pic/9a359fcac8114575a75d042a9de218b9.png"), // 巡店任务
    QUESTION_ORDER("https://oss-cool.coolstore.cn/notice_pic/206d78e98f9045f99890e4df607bf377.png"), // 检查项
    DISPLAY_TASK("https://oss-cool.coolstore.cn/notice_pic/cfa059a851a84c788134f533b3d5e279.jpg"), // 巡店任务
    ;


    private static final Map<String, PicUrlEnum> map = Arrays.stream(values()).collect(Collectors.toMap(PicUrlEnum::getValue, Function.identity()));

    private String value;

    PicUrlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PicUrlEnum parseValue(String value) {
        return map.get(value);
    }
}
