package com.coolcollege.intelligent.model.enterprise;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/9/17 16:27
 */
@Data
public class DeptIdDTO {

    /**
     * 部门id
     */
    private String id;

    /**
     * 父节点id
     */
    private String parentId;
}
