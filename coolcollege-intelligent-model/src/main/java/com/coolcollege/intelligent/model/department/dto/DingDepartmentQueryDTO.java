package com.coolcollege.intelligent.model.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 钉钉同步组织架构门店的查询类
 * @author 王春辉
 * @date 2020-07-22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDepartmentQueryDTO {
    /**
     * 部门列表（包含同步和删除门店）
     */
    private List<MonitorDeptDTO> departments;
    /**
     * 运营列表
     */
    private MonitorDeptTypeDTO operator;
    /**
     * 店长列表
     */
    private MonitorDeptTypeDTO shopowner;
    /**
     * 店员列表
     */
    private MonitorDeptTypeDTO  clerk;
    /**
     * 时间戳
     */
    private String timestamp;

    @Override
    public String toString() {
        return "DingDepartmentQueryDTO{" +
                "departments=" + departments +
                ", operator=" + operator +
                ", shopowner=" + shopowner +
                ", clerk=" + clerk +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
