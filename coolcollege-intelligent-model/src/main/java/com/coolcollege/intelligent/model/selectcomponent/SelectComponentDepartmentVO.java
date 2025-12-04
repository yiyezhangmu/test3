package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中人员信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentDepartmentVO {
    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 人员数
     */
    private Integer userCount;

    /**
     * 根目录->上级的区域
     */
    List<DepartmentInfoVO> departmentInfos;
}
