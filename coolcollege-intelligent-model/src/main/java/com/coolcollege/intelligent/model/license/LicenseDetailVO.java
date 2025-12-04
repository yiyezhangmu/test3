package com.coolcollege.intelligent.model.license;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/20 15:05
 */
@Data
public class LicenseDetailVO {
    /**
     * 证照类型id
     */
    private Long licenseTypeId;

    /**
     * 证照类型名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 必填：0否，1是
     */
    private Boolean required;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 水印设置
     */
    private String waterMark;

    /**
     * 证照类型扩展字段列表
     */
    private List<LcLicenseTypeExtendFieldVO> lcLicenseTypeExtendFieldVOList;

}
