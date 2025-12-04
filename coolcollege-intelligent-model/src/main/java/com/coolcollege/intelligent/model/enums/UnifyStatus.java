package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/28 11:32
 */
public enum UnifyStatus {

    /**
     * 任务类型
     */
    NOSTART("nostart", "未开始"),
    ONGOING("ongoing", "进行中"),
    COMPLETE("complete", "已完成"),
            ;

    private static final Map<String, UnifyStatus> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyStatus::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyStatus getByCode(String code) {
        return map.get(code);
    }
}
