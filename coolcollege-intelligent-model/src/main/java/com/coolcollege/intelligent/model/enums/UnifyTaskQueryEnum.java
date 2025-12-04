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
 * @date ：Created in 2020/10/28 14:23
 */
public enum  UnifyTaskQueryEnum {

    /**
     * 子任务
     */
    HANDLE("handle", "待处理"),
    APPROVER("approver", "待审批"),
    RECHECK("recheck", "待复审"),
    /**
     * 父任务
     */
    NOSTART("nostart", "未开始"),
    ONGOING("ongoing", "进行中"),
    /**
     * 共用
     */
    ALL("all", "全部"),
    COMPLETE("complete", "已完成"),
            ;

    public static final Map<String, UnifyTaskQueryEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskQueryEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTaskQueryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTaskQueryEnum getByCode(String code) {
        return map.get(code);
    }
}
