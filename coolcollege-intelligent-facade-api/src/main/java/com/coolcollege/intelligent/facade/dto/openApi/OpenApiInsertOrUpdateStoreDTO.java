package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 17:09
 * @Version 1.0
 */
@Data
public class OpenApiInsertOrUpdateStoreDTO {

    private String avatar;

    private String businessHours;

    private String latitude;

    private String longitude;

    private String locationAddress;

    private String parentThirdDeptId;

    private String storeAcreage;

    private String storeAddress;

    private String storeBandWidth;

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

    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String county;

    private Boolean isDelete;

    public boolean check(){
        boolean isDeleted = Objects.isNull(this.isDelete) ? Boolean.FALSE : this.isDelete;
        if(!isDeleted && StringUtils.isAnyBlank(storeName, thirdDeptId)){
            return false;
        }
        if(isDeleted && StringUtils.isAnyBlank(thirdDeptId)){
            return false;
        }
        return true;
    }
}
