package com.coolcollege.intelligent.model.department;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DeptNode
 * @Description 部门节点
 */
@Data
public class DeptNode {

    private String id;

    private String departmentName;

    private String parentId;

    private Long userCount = 0L;

    private List<DeptNode> children;
}
