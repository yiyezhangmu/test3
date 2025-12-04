package com.coolcollege.intelligent.model.homepage;

import lombok.Data;

/**
 * 首页实体对象
 * @ClassName  HomepageDO
 * @Description 首页实体对象
 * @author Aaron
 */
@Data
public class HomepageDO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店id
     */
    private String storeName;

    /**
     * 门店地址
     */
    private  String storeAddress;

    /**
     * 经度
     */
    private  String longitude;

    /**
     * 纬度
     */
    private  String latitude;

    /**
     * 距离
     */
    private  Double distance;

}
