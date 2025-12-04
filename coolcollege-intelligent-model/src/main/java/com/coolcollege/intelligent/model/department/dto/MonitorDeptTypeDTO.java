package com.coolcollege.intelligent.model.department.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/9/16 15:54
 */
@Data
public class MonitorDeptTypeDTO {

    /**
     * 同步的类型
     * @see com.coolcollege.intelligent.common.enums.store.DingSyncType
     */
    private String type;

    /**
     * 监控的指定类型的id列表
     */
    private List<Long> ids;

    @Override
    public String toString() {
        return "MonitorDeptTypeDTO{" +
                "type='" + type + '\'' +
                ", positionIds=" + ids +
                '}';
    }
}
