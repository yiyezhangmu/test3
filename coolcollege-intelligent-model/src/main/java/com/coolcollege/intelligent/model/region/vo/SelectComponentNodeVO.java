package com.coolcollege.intelligent.model.region.vo;

import com.coolcollege.intelligent.model.selectcomponent.DepartmentInfoVO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/8 10:23
 * @Version 1.0
 */
@Data
public class SelectComponentNodeVO {

    private String regionId;

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
    List<RegionInfoVO> regionInfos;
}
