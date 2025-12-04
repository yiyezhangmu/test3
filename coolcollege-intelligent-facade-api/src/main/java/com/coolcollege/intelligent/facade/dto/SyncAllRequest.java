package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.util.List;


/**
 * @author byd
 */
@Data
public class SyncAllRequest {

    /**
     * 企业id
     */
    private String enterpriseId;


    /**
     * 组织架构部门列表
     */
    private List<RegionDTO> deptList;

    /**
     * 三方部门列表
     */
    private List<ThirdDepartmentDTO> thirdDeptList;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 组织架构id
     */
    private String unitId;

    /**
     * 来源 EHR:百丽
     */
    private String source;

    /**
     * 日志记录id
     */
    private Long logId;

    /**
     * 区域Id
     */
    private Long regionid;
}
