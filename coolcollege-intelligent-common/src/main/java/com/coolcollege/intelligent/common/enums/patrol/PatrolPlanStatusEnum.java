package com.coolcollege.intelligent.common.enums.patrol;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanStatusEnum
 * @Description:
 * @date 2024-09-05 17:26
 */
public enum PatrolPlanStatusEnum {

    // 0待审批，1待处理，2已驳回，3已完成
    WAIT_AUDIT(0, "待审批"),
    WAIT_HANDLE(1, "待完成巡店"),
    REJECT(2, "已驳回"),
    FINISHED(3, "已完成"),
    ;


    private Integer code;

    private String desc;

    PatrolPlanStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isAuditStatus(Integer auditStatus){
        return WAIT_AUDIT.getCode().equals(auditStatus);
    }

    public static boolean isEditStatus(Integer auditStatus){
        if(WAIT_AUDIT.getCode().equals(auditStatus) || REJECT.getCode().equals(auditStatus)){
            return true;
        }
        return false;
    }

    public static final Map<Integer, String> MAP = Arrays.stream(PatrolPlanStatusEnum.values())
            .collect(Collectors.toMap(
                    PatrolPlanStatusEnum::getCode,
                    PatrolPlanStatusEnum::getDesc
            ));
}
