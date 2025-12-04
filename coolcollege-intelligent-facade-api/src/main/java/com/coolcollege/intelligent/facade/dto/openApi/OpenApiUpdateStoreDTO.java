package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;


/**
 * @Author suzhuhong
 * @Date 2022/7/18 17:09
 * @Version 1.0
 */
@Data
public class OpenApiUpdateStoreDTO {

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门头照
     */
    private String avatar;

    /**
     * 地区id
     */
    private Long regionId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 门店地址
     */
    private String storeAddress;

    /**
     * 门店经纬度地址
     */
    private String locationAddress;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 业务时间
     */
    private String businessHours;

    /**
     * 门店面积
     */
    private String storeAcreage;

    /**
     * 带宽
     */
    private String storeBandWidth;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String storeStatus;

    /**
     * 门店开业日期
     */
    private String openDate;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 第三方管理唯一key
     */
    private String thirdDeptId;

    /**
     * 门店编号
     */
    private String storeNum;


    public boolean checkParam(){
        if(StringUtils.isAllBlank(storeId, thirdDeptId, storeNum)){

            return false;
        }
        if(!StringUtils.isAllBlank(longitude, latitude) && StringUtils.isAnyBlank(longitude, latitude)){
            return false;
        }
        return !StringUtils.isAllBlank(storeName, province, city, county, storeAddress, locationAddress, telephone, businessHours, storeAcreage, storeBandWidth, remark, storeStatus, openDate);
    }



}
