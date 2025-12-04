package com.coolcollege.intelligent.model.department.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/9 16:29
 */
@Data
public class QueryDeptChildDTO {

    private String id;

    private String parentId;

    private String name;
    /**
     * 部门次序
     */
    private Integer departOrder;

    private String path;
}
