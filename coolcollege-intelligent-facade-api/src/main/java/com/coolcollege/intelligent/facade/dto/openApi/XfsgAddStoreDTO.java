package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * 大区、战区、小区、门店
 * 门店详细信息接口（父级ehr编码、父级门店主数据小区编码）
 * 门店详细信息接口（父级ehr编码、父级门店主数据小区编码）
 * @Author wxp
 * @Date 2024/3/25 17:09
 * @Version 1.0
 */
@Data
public class XfsgAddStoreDTO {

    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 门店code
     */
    private String storeCode;
    /**
     * 父部门编码
     */
    private String parentDeptCode;
    /**
     * 父部门名称
     */
    private String parentDeptName;
    /**
     * 维度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 门店地址
     */
    private String storeAdd;

    /**
     * 门店开业日期  2016-04-20
     */
    private String openDate;

    /**
     * 门店营业时间  09:00~21:00
     */
    private String businessHours;

}
