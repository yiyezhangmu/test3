package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/2/28 17:27
 * @Version 1.0
 */
@Data
public class MySubordinatesDTO {

    /**
     * 映射ID
     */
    String regionId;

    String regionName;

    String personalId;

    String personalName;
    /**
     * 节点类型 区域 人员
     */
    String nodeType;
}
