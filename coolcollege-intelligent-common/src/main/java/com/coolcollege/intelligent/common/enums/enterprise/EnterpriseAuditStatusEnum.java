package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 企业审核状态
 * @author xugk
 */
public enum EnterpriseAuditStatusEnum {

    /**
     * 待审核
     */
    AUDIT_PENDING(0),
    /**
     * 审核通过
     */
    AUDIT_PASSED(1),
    /**
     * 审核不通过
     */
    AUDIT_FAILED(2);

    private static final Map<Integer, EnterpriseAuditStatusEnum> map = Arrays.stream(values()).collect(Collectors.toMap(EnterpriseAuditStatusEnum::getValue, Function.identity()));

    private Integer value;

    EnterpriseAuditStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static EnterpriseAuditStatusEnum parseValue(Integer value) {
        return map.get(value);
    }
}
