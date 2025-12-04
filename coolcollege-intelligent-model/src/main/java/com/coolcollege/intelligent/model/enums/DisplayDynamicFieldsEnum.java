package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/21 16:38
 */
public enum DisplayDynamicFieldsEnum {
    TASK_TYPE("类型","taskType"),
    STORE_AREA_NAME("门店区域","storeAreaName"),
    STORE_NAME("门店名称","storeName"),
    TABLE_NAME("检查表","tableName"),
    META_COLUMN_NUM("总检查项数","metaColumnNum"),
    HANDLER_USE_NAME("处理人","handleUserName"),
    APPROVE_USER_NAME("审批人","approveUserName"),
    RECHECK_USER_NAME("复审人","recheckUserName"),
    DONE_TIME("实际结束时间","doneTime"),
    CHECK_TIME("任务完成时长","checkTime"),
    TASK_NAME("任务名称","taskName"),
    VALID_TIME("有效期","validTime"),
    TASK_DESC("任务说明","taskDesc"),
    REGION_NAME("门店得分","score"),
    REMARK("门店评价","remark"),
    STATUS("流程状态","status"),
    OVERDUE("是否过期完成","overdue"),
    ;

    private String name;

    private String fieldName;

    private static final Map<String, DisplayDynamicFieldsEnum> map = Arrays.stream(values()).collect(Collectors.toMap(DisplayDynamicFieldsEnum::getName, Function.identity()));


    DisplayDynamicFieldsEnum(String name, String fieldName) {
        this.name = name;
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static DisplayDynamicFieldsEnum getEnum(String name){
        return map.get(name);
    }

    public static List<String> nameList(){
        return Arrays.stream(values()).map(data -> data.getName()).collect(Collectors.toList());
    }
}
