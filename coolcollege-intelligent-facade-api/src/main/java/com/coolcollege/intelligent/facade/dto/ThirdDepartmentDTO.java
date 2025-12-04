package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2024-04-02 04:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdDepartmentDTO {

    private Long id;

    /**
     * 三方部门code
     */
    private String departmentCode;

    /**
     * 三方部门名称
     */
    private String departmentName;

    /**
     * 部门负责人 鲜丰水果是工号
     */
    private String deptPrincipal;

    /**
     * 父部门code
     */
    private String parentDepartmentCode;

}