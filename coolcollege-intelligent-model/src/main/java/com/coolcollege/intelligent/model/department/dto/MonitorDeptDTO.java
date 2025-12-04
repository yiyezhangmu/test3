package com.coolcollege.intelligent.model.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/9/16 15:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorDeptDTO {
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 门店id
     */
    private List<String> storeIds;
    /**
     * 删除的门店id
     */
    private List<String> deleteIds;
}
