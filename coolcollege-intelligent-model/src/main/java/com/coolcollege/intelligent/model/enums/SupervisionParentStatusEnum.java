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
 * @Author suzhuhong
 * @Date 2023/3/7 14:39
 * @Version 1.0
 */
public enum SupervisionParentStatusEnum {

    /**
     * 督导子任务、门店任务状态
     */
    NOT_STARTED(1, "未开始"),
    ONGOING(2, "进行中"),
    OVER(3, "已结束"),
    CANCEL(4, "已取消"),
    FAILURE(5, "已失效"),
    ;

    private static final Map<Integer, SupervisionParentStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(SupervisionParentStatusEnum::getStatus, Function.identity()));


    public static final List<SupervisionParentStatusEnum> getSupervisionParentStatusEnumList(List<Integer> statusList){
        ArrayList<SupervisionParentStatusEnum> supervisionTaskPriorityEnums = new ArrayList<>();
        if (CollectionUtils.isEmpty(statusList)){
            return supervisionTaskPriorityEnums;
        }
        for (Integer code:statusList) {
            SupervisionParentStatusEnum supervisionParentStatusEnum = map.get(code);
            if (supervisionParentStatusEnum!=null){
                supervisionTaskPriorityEnums.add(supervisionParentStatusEnum);
            }

        }
        return supervisionTaskPriorityEnums;
    }

    private Integer status;
    private String desc;

    SupervisionParentStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static SupervisionParentStatusEnum getByCode(Integer code) {
        return map.get(code);
    }

}
