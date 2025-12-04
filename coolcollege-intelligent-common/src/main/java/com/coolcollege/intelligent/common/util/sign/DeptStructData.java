package com.coolcollege.intelligent.common.util.sign;

import lombok.Data;

/**
 * @author byd
 * @date 2022-11-08 19:15
 */
@Data
public class DeptStructData {

    /**
     * 业务单位
     */
    private String bu;
    /**
     * 业务单位描述
     */
    private String buDes;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 部门描述
     */
    private String deptDes;
    /**
     * 部门短描述
     */
    private String deptShortDes;
    /**
     * 部门范围
     */
    private String deptRange;
    /**
     * 部门熟悉
     */
    private String deptPro;
    /**
     * 状态A有效,1无效
     */
    private String status;
    /**
     * 父节点
     */
    private String parentId;
    /**
     * ics编码
     */
    private String icsCode;
    /**
     * 部门第一负责人
     */
    private String managerId;
}
