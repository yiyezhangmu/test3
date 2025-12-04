package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中人员信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class DepartmentInfoVO {
    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String name;
}
