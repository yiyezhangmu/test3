package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 17:09
 * @Version 1.0
 */
@Data
public class OpenApiAddStoreDTO {

    private String avatar;

    private String businessHours;

    private String longitudeLatitude;

    private String locationAddress;

    private Long regionId;

    private String storeAcreage;

    private String storeAddress;

    private String storeBandWidth;

    private String remark;

    private String storeName;

    private String storeNum;

    private String storeStatus;

    private String telephone;

    private String storeId;

    /**
     * 第三方管理唯一key
     */
    private String thirdDeptId;

    /**
     * 门店开业日期
     */
    private String openDate;
}
