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
 * @date ：Created in 2020/7/24 15:15
 */
public enum TaskQueryEnum {
    /**
     * 任务类型
     */
    PENDING("pending", "待处理"),
    CC("cc", "抄送"),
    CREATE("create", "我创建的"),
    COMPLETE("complete", "已完成"),
    ALL("all", "全部"),
            ;

    public static final Map<String, TaskQueryEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(TaskQueryEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    TaskQueryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskQueryEnum getByCode(String code) {
        return map.get(code);
    }
}
