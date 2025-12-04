package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/27
 */
@Data
public class OpenApiEnterpriseVO {

    /**
     * 企业主键
     */
    private String id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;
}
