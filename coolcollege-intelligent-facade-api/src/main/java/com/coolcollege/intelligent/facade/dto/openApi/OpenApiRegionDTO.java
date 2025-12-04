package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:32
 * @Version 1.0
 */
@Data
public class OpenApiRegionDTO {

    /**
     * 父任务ID
     */
    private Long parentId;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 第三方管理唯一key
     */
    private String thirdDeptId;
}
