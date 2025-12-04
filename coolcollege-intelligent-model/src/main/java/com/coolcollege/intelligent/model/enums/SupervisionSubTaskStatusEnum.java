package com.coolcollege.intelligent.model.enums;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 督导子任务、门店任务状态枚举
 * @Author suzhuhong
 * @Date 2023/2/28 20:31
 * @Version 1.0
 */
public enum SupervisionSubTaskStatusEnum {

    /**
     * 督导子任务、门店任务状态
     */
    TODO(0, "待完成"),
    COMPLETE(1, "已完成"),
    APPROVAL(4, "待审批"),
    CANCEL(5, "已取消"),
    ;

    private static final Map<Integer, SupervisionSubTaskStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(SupervisionSubTaskStatusEnum::getStatus, Function.identity()));


    public static final List<SupervisionSubTaskStatusEnum> getSupervisionSubTaskStatusEnumList(List<Integer> codeList){
        List<SupervisionSubTaskStatusEnum> supervisionSubTaskStatusEnums = new ArrayList<>();
        if (CollectionUtils.isEmpty(codeList)){
            return supervisionSubTaskStatusEnums;
        }
        for (Integer code:codeList) {
            SupervisionSubTaskStatusEnum supervisionSubTaskStatusEnum = map.get(code);
            if (supervisionSubTaskStatusEnum!=null){
                supervisionSubTaskStatusEnums.add(supervisionSubTaskStatusEnum);
            }

        }
        return supervisionSubTaskStatusEnums;
    }
    private Integer status;
    private String desc;

    SupervisionSubTaskStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static SupervisionSubTaskStatusEnum getByCode(Integer code) {
        return map.get(code);
    }
}
