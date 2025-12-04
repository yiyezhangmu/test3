package com.coolcollege.intelligent.model.region.dto;

import com.coolcollege.intelligent.model.department.DeptNode;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/8 10:51
 * @Version 1.0
 */
@Data
public class RegionAndStoreNode {

    private String id;

    private String name;

    private String parentId;

    private Long userCount = 0L;
}
